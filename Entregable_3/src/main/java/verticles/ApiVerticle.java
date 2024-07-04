package verticles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import types.EstadoActuador;
import types.Actuador;
import types.Grupo;
import types.Placa;
import types.SensorHumedad;
import types.SensorTemp;
import types.ValorHumedad;
import types.ValorTemp;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;


public class ApiVerticle extends AbstractVerticle
{
	private Gson gson;
	private MySQLPool mySQLPool;
	
	public void start(Promise<Void> startFuture) {
		MySQLConnectOptions mySQLConnectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("bd_proyecto").setUser("root").setPassword("root");
		
		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
		mySQLPool = MySQLPool.pool(vertx, mySQLConnectOptions, poolOptions);
	
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		
	// Defining the router object
		HttpServer httpServer = vertx.createHttpServer();
		Router router = Router.router(vertx);
		
		// Handling any server startup result
		httpServer.requestHandler(router::handle).listen(80, result -> {
			if (result.succeeded()) {
				System.out.println("API Rest is listening on port 80");
				startFuture.complete();
			} else {
				startFuture.fail(result.cause());
			}
		});
		router.route("/api*").handler(BodyHandler.create());
		
		router.get("/api/SensorTemp/:id_SensorTemp").handler(this::getSensorTempByID);
		router.post("/api/SensorTemp/add").handler(this::addSensorTemp);
	
		router.get("/api/SensorHumedad/:id_SensorHumedad").handler(this::getSensorHumedadByID);
		router.post("/api/SensorHumedad/add").handler(this::addSensorHumedad);
		/*
		router.get("/api/Actuador/:id_Actuador").handler(this::getActuadorByID);
		router.post("/api/Actuador/add").handler(this::addActuador);
		
		router.get("/api/Grupo/:id_Grupo").handler(this::getGrupoByID);
		router.post("/api/Grupo/add").handler(this::addGrupo);
		
		router.get("/api/Placa/:id_Placa").handler(this::getPlacaByID);
		router.post("/api/Placa/add").handler(this::addPlaca);

		router.get("/api/Estados/:id_Actuador").handler(this::getEstadosByActuadorID);
		router.get("/api/Estados/last/:id_Actuador").handler(this::getLastEstadoByActuadorID);
		router.post("/api/Estados/add").handler(this::addEstadoActuador);
		
		router.get("/api/ValorHumedad/:id_SensorHumedad").handler(this::getValorHumedadBySensorHumedadID);
		router.get("/api/ValorHumedad/last/:id_SensorHumedad").handler(this::getLastValorHumedadBySensorHumedadID);
		router.post("/api/ValorHumedad/add").handler(this::addValorHumedad);
		
		router.get("/api/ValorTemp/:id_SensorTemp").handler(this::getValorTempBySensorTempID);
		router.get("/api/ValorTemp/last/:id_SensorTemp").handler(this::getLastValorTempBySensorTempID);
		router.post("/api/ValorTemp/add").handler(this::addValorTemp);*/
	}

	private void getSensorTempByID(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM SensorTemp WHERE id_SensorTemp = '" + routingContext.request().getParam("id_SensorTemp") + "'")
			.execute().onComplete(res -> {
					if (res.succeeded()) {	
						RowSet<Row> resultSet = res.result();
						JsonArray result = new JsonArray();
						
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new SensorTemp(row.getInteger("id_SensorTemp"),
									row.getInteger("id_Placa"))));
							
						}
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(result.encodePrettily());
						}else {
							routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
							.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}
				});
	}
	
	private void addSensorTemp(RoutingContext routingContext) {
		SensorTemp sensor = gson.fromJson(routingContext.getBodyAsString(), SensorTemp.class);
		//System.out.println(routingContext.get("id_SensorTemp") );
		mySQLPool.query("INSERT INTO SensorTemp VALUES ("+ String.valueOf(sensor.getId_SensorTemp())+"," + String.valueOf(sensor.getId_Placa()) +")").
		execute().onComplete(res -> {
			if (res.succeeded()) {
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(sensor));
			}else {
				routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
				.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
			}

			});
	}
	
	
	private void getSensorHumedadByID(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM SensorTemp WHERE id_SensorHumedad = '" + routingContext.request().getParam("id_SensorHumedad") + "'")
		.execute().onComplete(res -> {
				if (res.succeeded()) {	
					RowSet<Row> resultSet = res.result();
					JsonArray result = new JsonArray();
					
					for (Row row : resultSet) {
						result.add(JsonObject.mapFrom(new SensorHumedad(row.getInteger("id_SensorHumedad"),
								row.getInteger("id_Placa"))));
						
					}
					routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					.end(result.encodePrettily());
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
				}
			});
	}
	
	private void addSensorHumedad(RoutingContext routingContext) {
		SensorHumedad sensor = gson.fromJson(routingContext.getBodyAsString(), SensorHumedad.class);
		memoria_SensorHumedad.put(sensor.getSensorhumedad_id(), sensor);
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(memoria_SensorHumedad.get(sensor.getSensorhumedad_id())));
	}
	/*
	private void getActuadorByID(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(memoria_Actuador.get(Integer.parseInt(routingContext.request().getParam("id_Actuador")))));
	}
	
	private void addActuador(RoutingContext routingContext) {
		Actuador actuador = gson.fromJson(routingContext.getBodyAsString(), Actuador.class);
		memoria_Actuador.put(actuador.getid_Actuador(), actuador);
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(memoria_Actuador.get(actuador.getid_Actuador())));
	}
	
	private void getGrupoByID(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(memoria_Grupo.get(Integer.parseInt(routingContext.request().getParam("id_Grupo")))));
	}
	
	private void addGrupo(RoutingContext routingContext) {
		Grupo grupo = gson.fromJson(routingContext.getBodyAsString(), Grupo.class);
		memoria_Grupo.put(grupo.getId_Grupo(), grupo);
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(memoria_Grupo.get(grupo.getId_Grupo())));
	}
	
	private void getPlacaByID(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(memoria_Placa.get(Integer.parseInt(routingContext.request().getParam("id_Placa")))));
	}
	
	private void addPlaca(RoutingContext routingContext) {
		Placa placa = gson.fromJson(routingContext.getBodyAsString(), Placa.class);
		memoria_Placa.put(placa.getId_Placa(), placa);
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(memoria_Placa.get(placa.getId_Placa())));
	}

	private void getEstadosByActuadorID(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(memoria_Estado.get(Integer.parseInt(routingContext.request().getParam("id_Actuador")))));
	}
	
	private void getLastEstadoByActuadorID(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(memoria_Estado.get(Integer.parseInt(routingContext.request().getParam("id_Actuador"))).getLast()));
	}
	
	private void addEstadoActuador(RoutingContext routingContext) {
		EstadoActuador estado = gson.fromJson(routingContext.getBodyAsString(), EstadoActuador.class);
		if(memoria_Estado.containsKey(estado.getId_Actuador())) {
			memoria_Estado.get(estado.getId_Actuador()).add(estado);
		
			//lista2.add(estado);
			//memoria_Estado.put(estado.getId_Actuador(), lista);
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(estado));
		}
		else {
			ArrayList <EstadoActuador> lista = new ArrayList<EstadoActuador>();
			lista.add(estado);
			memoria_Estado.put(estado.getId_Actuador(), lista);
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(estado));
		}
	}
	
	private void getValorHumedadBySensorHumedadID(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(memoria_ValorHumedad.get(Integer.parseInt(routingContext.request().getParam("id_SensorHumedad")))));
	}
	
	private void getLastValorHumedadBySensorHumedadID(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(memoria_ValorHumedad.get(Integer.parseInt(routingContext.request().getParam("id_SensorHumedad"))).getLast()));
	}
	
	private void addValorHumedad(RoutingContext routingContext) {
		ValorHumedad valor = gson.fromJson(routingContext.getBodyAsString(), ValorHumedad.class);
		if(memoria_ValorHumedad.containsKey(valor.getId_SensorHumedad())) {
			memoria_ValorHumedad.get(valor.getId_SensorHumedad()).add(valor);
		
			//lista2.add(estado);
			//memoria_Estado.put(estado.getId_Actuador(), lista);
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(valor));
		}
		else {
			ArrayList <ValorHumedad> lista = new ArrayList<ValorHumedad>();
			lista.add(valor);
			memoria_ValorHumedad.put(valor.getId_SensorHumedad(), lista);
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(valor));
		}
		
	}
	
	private void getValorTempBySensorTempID(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(memoria_ValorTemp.get(Integer.parseInt(routingContext.request().getParam("id_SensorTemp")))));
	}
	
	private void getLastValorTempBySensorTempID(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(memoria_ValorTemp.get(Integer.parseInt(routingContext.request().getParam("id_SensorTemp"))).getLast()));
	}
	
	private void addValorTemp(RoutingContext routingContext) {
		ValorTemp valor = gson.fromJson(routingContext.getBodyAsString(), ValorTemp.class);
		if(memoria_ValorTemp.containsKey(valor.getId_SensorTemp())) {
			memoria_ValorTemp.get(valor.getId_SensorTemp()).add(valor);
		
			//lista2.add(estado);
			//memoria_Estado.put(estado.getId_Actuador(), lista);
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(valor));
		}
		else {
			ArrayList <ValorTemp> lista = new ArrayList<ValorTemp>();
			lista.add(valor);
			memoria_ValorTemp.put(valor.getId_SensorTemp(), lista);
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(valor));
		}
	}*/
}