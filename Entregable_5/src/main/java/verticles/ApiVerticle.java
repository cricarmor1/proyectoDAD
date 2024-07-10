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
		router.get("/api/SensorTemp/all/:id_Placa").handler(this::getSensorTempByPlacaID);
		router.post("/api/SensorTemp/add").handler(this::addSensorTemp);
	
		router.get("/api/SensorHumedad/:id_SensorHumedad").handler(this::getSensorHumedadByID);
		router.get("/api/SensorHumedad/all/:id_Placa").handler(this::getSensorHumedadByPlacaID);
		router.post("/api/SensorHumedad/add").handler(this::addSensorHumedad);
		
		router.get("/api/Actuador/:id_Actuador").handler(this::getActuadorByID);
		router.get("/api/Actuador/all/:id_Placa").handler(this::getActuadorByPlacaID);
		router.post("/api/Actuador/add").handler(this::addActuador);
		
		router.get("/api/Grupo/:id_Grupo").handler(this::getGrupoByID);
		router.get("/api/Grupo/all/:placeholder").handler(this::getGrupoAll);
		router.post("/api/Grupo/add").handler(this::addGrupo);
		router.put("/api/Grupo/update/:mqtt_ch").handler(this::updateGrupoByMqtt);
		router.get("/api/Grupo/mqtt/:mqtt_ch").handler(this::getGrupoByMqtt);
		router.get("/api/Grupo/lasttemp/:mqtt_ch/:timestamp").handler(this::getLastTempByMqttTimestamp);
		router.get("/api/Grupo/lasthum/:mqtt_ch/:timestamp").handler(this::getLastHumByMqttTimestamp);
		router.get("/api/Grupo/lasttemp/:mqtt_ch").handler(this::getLastTempByMqtt);
		router.get("/api/Grupo/lasthum/:mqtt_ch").handler(this::getLastHumByMqtt);
		
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
		router.post("/api/ValorTemp/add").handler(this::addValorTemp);
		vertx.deployVerticle(MqttVerticle.class.getName());
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
	
	private void getSensorTempByPlacaID(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM SensorTemp WHERE id_Placa = '" + routingContext.request().getParam("id_Placa") + "'")
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
		mySQLPool.query("SELECT * FROM SensorHumedad WHERE id_SensorHumedad = '" + routingContext.request().getParam("id_SensorHumedad") + "'")
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
	
	private void getSensorHumedadByPlacaID(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM SensorHumedad WHERE id_Placa = '" + routingContext.request().getParam("id_Placa") + "'")
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
		//System.out.println(routingContext.get("id_SensorTemp") );
		mySQLPool.query("INSERT INTO SensorHumedad VALUES ("+ String.valueOf(sensor.getId_SensorHumedad())+"," + String.valueOf(sensor.getId_Placa()) +")").
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
	
	private void getActuadorByID(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM Actuador WHERE id_Actuador = '" + routingContext.request().getParam("id_Actuador") + "'")
		.execute().onComplete(res -> {
				if (res.succeeded()) {	
					RowSet<Row> resultSet = res.result();
					JsonArray result = new JsonArray();
					
					for (Row row : resultSet) {
						result.add(JsonObject.mapFrom(new Actuador(row.getInteger("id_Actuador"),
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
	
	private void getActuadorByPlacaID(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM Actuador WHERE id_Placa = '" + routingContext.request().getParam("id_Placa") + "'")
			.execute().onComplete(res -> {
					if (res.succeeded()) {	
						RowSet<Row> resultSet = res.result();
						JsonArray result = new JsonArray();
						
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new Actuador(row.getInteger("id_Actuador"),
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
	
	
	private void addActuador(RoutingContext routingContext) {
		Actuador actuador = gson.fromJson(routingContext.getBodyAsString(), Actuador.class);
		//System.out.println(routingContext.get("id_SensorTemp") );
		mySQLPool.query("INSERT INTO Actuador VALUES ("+ String.valueOf(actuador.getId_Actuador())+"," + String.valueOf(actuador.getId_Placa()) +")").
		execute().onComplete(res -> {
			if (res.succeeded()) {
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(actuador));
			}else {
				routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
				.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
			}

			});
	}
	
	private void getGrupoByID(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM Grupo WHERE id_Grupo = '" + routingContext.request().getParam("id_Grupo") + "'")
		.execute().onComplete(res -> {
				if (res.succeeded()) {
					System.out.println(res.result().size());
					RowSet<Row> resultSet = res.result();
					JsonArray result = new JsonArray();
					
					for (Row row : resultSet) {
						result.add(JsonObject.mapFrom(new Grupo(row.getInteger("id_Grupo"),
								row.getString("mqtt_ch"), row.getLong("timestampTemp"), row.getLong("timestampHum"))));
						
					}
					routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					.end(result.encodePrettily());
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
				}
			});
	}
	
	private void getGrupoAll(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM Grupo")
		.execute().onComplete(res -> {
				if (res.succeeded()) {	
					System.out.println(res.result().size());
					RowSet<Row> resultSet = res.result();
					JsonArray result = new JsonArray();
					
					for (Row row : resultSet) {
						result.add(JsonObject.mapFrom(new Grupo(row.getInteger("id_Grupo"),
								row.getString("mqtt_ch"), row.getLong("timestampTemp"), row.getLong("timestampHum"))));
						
					}
					routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					.end(result.encodePrettily());
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
				}
			});
	}
	
	private void getGrupoByMqtt(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM Grupo WHERE mqtt_ch = '" + routingContext.request().getParam("mqtt_ch") + "'")
		.execute().onComplete(res -> {
				if (res.succeeded()) {	
					RowSet<Row> resultSet = res.result();
					JsonArray result = new JsonArray();
					
					for (Row row : resultSet) {
						result.add(JsonObject.mapFrom(new Grupo(row.getInteger("id_Grupo"),
								row.getString("mqtt_ch"), row.getLong("timestampTemp"), row.getLong("timestampHum"))));
						
					}
					routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					.end(result.encodePrettily());
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
				}
			});
	}
	
	private void addGrupo(RoutingContext routingContext) {
		Grupo grupo = gson.fromJson(routingContext.getBodyAsString(), Grupo.class);
		//System.out.println(routingContext.get("id_SensorTemp") );
		mySQLPool.query("INSERT INTO Grupo VALUES ("+ String.valueOf(grupo.getId_Grupo())+"," + String.valueOf(grupo.getMqtt_ch())+",null,null" +")").
		execute().onComplete(res -> {
			if (res.succeeded()) {
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(grupo));
			}else {
				routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
				.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
			}

			});
	}
	
	private void updateGrupoByMqtt(RoutingContext routingContext) {
		Grupo grupo = gson.fromJson(routingContext.getBodyAsString(), Grupo.class);
		mySQLPool.query("UPDATE Grupo SET id_Grupo = '"+grupo.getId_Grupo()+ "', mqtt_ch = '" + grupo.getMqtt_ch()+"', timestampTemp = "
		+grupo.getTimestampTemp()+", timestampHum = " +grupo.getTimestampHum()+" WHERE mqtt_ch = '"
	+ routingContext.request().getParam("mqtt_ch") + "'")
		.execute().onComplete(res -> {
				if (res.succeeded()) {	
					
					routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
					.end(gson.toJson(grupo));;
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
				}
			});
	}
	private void getLastTempByMqttTimestamp(RoutingContext routingContext) {
		mySQLPool.query("SELECT id_ValorTemp, ValorTemp.id_SensorTemp, valorTemp, timestamp FROM ValorTemp LEFT JOIN SensorTemp ON ValorTemp.id_SensorTemp = SensorTemp.id_SensorTemp"
				+ " LEFT JOIN Placa ON SensorTemp.id_Placa = Placa.id_Placa "
				+ "WHERE (mqtt_ch = '" + routingContext.request().getParam("mqtt_ch") + "') AND (timestamp = '"+routingContext.request().getParam("timestamp")+"')")
		.execute().onComplete(res -> {
				if (res.succeeded()) {	
					RowSet<Row> resultSet = res.result();
					JsonArray result = new JsonArray();
					
					for (Row row : resultSet) {
						result.add(JsonObject.mapFrom(new ValorTemp(row.getInteger("id_ValorTemp"),
								row.getInteger("id_SensorTemp"), row.getFloat("valorTemp"), row.getLong("timestamp"))));
						
					}
					routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					.end(result.encodePrettily());
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
				}
			});
	}
	
	private void getLastTempByMqtt(RoutingContext routingContext) {
		mySQLPool.query("SELECT id_ValorTemp, ValorTemp.id_SensorTemp, valorTemp, timestamp FROM ValorTemp LEFT JOIN SensorTemp ON ValorTemp.id_SensorTemp = SensorTemp.id_SensorTemp"
				+ " LEFT JOIN Placa ON SensorTemp.id_Placa = Placa.id_Placa "
				+ "WHERE mqtt_ch = '" + routingContext.request().getParam("mqtt_ch") + "' ORDER BY timestamp DESC LIMIT 1")
		.execute().onComplete(res -> {
				if (res.succeeded()) {	
					RowSet<Row> resultSet = res.result();
					JsonArray result = new JsonArray();
					
					for (Row row : resultSet) {
						result.add(JsonObject.mapFrom(new ValorTemp(row.getInteger("id_ValorTemp"),
								row.getInteger("id_SensorTemp"), row.getFloat("valorTemp"), row.getLong("timestamp"))));
						
					}
					routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					.end(result.encodePrettily());
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
				}
			});
	}
	
	
	private void getLastHumByMqttTimestamp(RoutingContext routingContext) {
		mySQLPool.query("SELECT id_ValorHumedad, ValorHumedad.id_SensorHumedad, valorHumedad, timestamp FROM ValorHumedad LEFT JOIN SensorHumedad ON ValorHumedad.id_SensorHumedad = SensorHumedad.id_SensorHumedad"
				+ " LEFT JOIN Placa ON SensorHumedad.id_Placa = Placa.id_Placa "
				+ "WHERE (mqtt_ch = '" + routingContext.request().getParam("mqtt_ch") + "') AND (timestamp = '"+routingContext.request().getParam("timestamp")+"')")
		.execute().onComplete(res -> {
				if (res.succeeded()) {	
					RowSet<Row> resultSet = res.result();
					JsonArray result = new JsonArray();
					
					for (Row row : resultSet) {
						result.add(JsonObject.mapFrom(new ValorHumedad(row.getInteger("id_ValorHumedad"),
								row.getInteger("id_SensorHumedad"), row.getFloat("valorHumedad"), row.getLong("timestamp"))));
						
					}
					routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					.end(result.encodePrettily());
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
				}
			});
	}
	
	private void getLastHumByMqtt(RoutingContext routingContext) {
		mySQLPool.query("SELECT id_ValorHumedad, ValorHumedad.id_SensorHumedad, valorHumedad, timestamp FROM ValorHumedad LEFT JOIN SensorHumedad ON ValorHumedad.id_SensorHumedad = SensorHumedad.id_SensorHumedad"
				+ " LEFT JOIN Placa ON SensorHumedad.id_Placa = Placa.id_Placa "
				+ "WHERE mqtt_ch = '" + routingContext.request().getParam("mqtt_ch") + "' ORDER BY timestamp DESC LIMIT 1 ")
		.execute().onComplete(res -> {
				if (res.succeeded()) {	
					RowSet<Row> resultSet = res.result();
					JsonArray result = new JsonArray();
					
					for (Row row : resultSet) {
						result.add(JsonObject.mapFrom(new ValorHumedad(row.getInteger("id_ValorHumedad"),
								row.getInteger("id_SensorHumedad"), row.getFloat("valorHumedad"), row.getLong("timestamp"))));
						
					}
					routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					.end(result.encodePrettily());
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
				}
			});
	}
	

	
	private void getPlacaByID(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM Placa WHERE id_Placa = '" + routingContext.request().getParam("id_Placa") + "'")
		.execute().onComplete(res -> {
				if (res.succeeded()) {	
					RowSet<Row> resultSet = res.result();
					JsonArray result = new JsonArray();
					
					for (Row row : resultSet) {
						result.add(JsonObject.mapFrom(new Placa(row.getInteger("id_Placa"),
								row.getInteger("id_Grupo"), row.getString("mqtt_ch"))));
						
					}
					routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					.end(result.encodePrettily());
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
				}
			});
	}
	
	private void addPlaca(RoutingContext routingContext) {
		Placa placa = gson.fromJson(routingContext.getBodyAsString(), Placa.class);
		//System.out.println(routingContext.get("id_SensorTemp") );
		mySQLPool.query("INSERT INTO Placa VALUES ("+ String.valueOf(placa.getId_Placa())+"," + String.valueOf(placa.getId_Grupo()) +","+ String.valueOf(placa.getMqtt_ch()) +")").
		execute().onComplete(res -> {
			if (res.succeeded()) {
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(placa));
			}else {
				routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
				.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
			}

			});
	}
	
	private void getEstadosByActuadorID(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM EstadoActuador WHERE id_Actuador = '" + routingContext.request().getParam("id_Actuador") + "'")
		.execute().onComplete(res -> {
				if (res.succeeded()) {	
					RowSet<Row> resultSet = res.result();
					JsonArray result = new JsonArray();
					
					for (Row row : resultSet) {
						result.add(JsonObject.mapFrom(new EstadoActuador(row.getInteger("id_EstadoActuador"),
								row.getInteger("id_Actuador"), row.getFloat("estado"), row.getLong("timestamp"))));
						
					}
					routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					.end(result.encodePrettily());
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
				}
			});
	}
	
	private void getLastEstadoByActuadorID(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM EstadoActuador WHERE id_Actuador = '" + routingContext.request().getParam("id_Actuador")+"'" + "ORDER BY timestamp DESC LIMIT 1")
		.execute().onComplete(res -> {
				if (res.succeeded()) {	
					RowSet<Row> resultSet = res.result();
					JsonArray result = new JsonArray();
					
					for (Row row : resultSet) {
						result.add(JsonObject.mapFrom(new EstadoActuador(row.getInteger("id_EstadoActuador"),
								row.getInteger("id_Actuador"), row.getFloat("estado"), row.getLong("timestamp"))));
						
					}
					routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					.end(result.encodePrettily());
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
				}
			});
	}
	
	private void addEstadoActuador(RoutingContext routingContext) {
		EstadoActuador estadoactuador = gson.fromJson(routingContext.getBodyAsString(), EstadoActuador.class);
		//System.out.println(routingContext.get("id_SensorTemp") );
		mySQLPool.query("INSERT INTO EstadoActuador VALUES ("+ String.valueOf(estadoactuador.getId_EstadoActuador())+"," + String.valueOf(estadoactuador.getId_Actuador()) +","
					+ String.valueOf(estadoactuador.getEstado())+","+ String.valueOf(estadoactuador.getTimestamp())+")").
		execute().onComplete(res -> {
			if (res.succeeded()) {
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(estadoactuador));
			}else {
				routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
				.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
			}

			});
	}
	
	private void getValorHumedadBySensorHumedadID(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM ValorHumedad WHERE id_SensorHumedad = '" + routingContext.request().getParam("id_SensorHumedad") + "'")
		.execute().onComplete(res -> {
				if (res.succeeded()) {	
					RowSet<Row> resultSet = res.result();
					JsonArray result = new JsonArray();
					
					for (Row row : resultSet) {
						result.add(JsonObject.mapFrom(new ValorHumedad(row.getInteger("id_valorHumedad"),
								row.getInteger("id_SensorHumedad"), row.getFloat("valorHumedad"), row.getLong("timestamp"))));
						
					}
					routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					.end(result.encodePrettily());
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
				}
			});
	}
	
	private void getLastValorHumedadBySensorHumedadID(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM ValorHumedad WHERE id_SensorHumedad = '" + routingContext.request().getParam("id_SensorHumedad")+"'" + "ORDER BY timestamp DESC LIMIT 1")
		.execute().onComplete(res -> {
				if (res.succeeded()) {	
					RowSet<Row> resultSet = res.result();
					JsonArray result = new JsonArray();
					
					for (Row row : resultSet) {
						result.add(JsonObject.mapFrom(new ValorHumedad(row.getInteger("id_valorHumedad"),
								row.getInteger("id_SensorHumedad"), row.getFloat("valorHumedad"), row.getLong("timestamp"))));
						
					}
					routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					.end(result.encodePrettily());
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
				}
			});
	}
	
	private void addValorHumedad(RoutingContext routingContext) {
		ValorHumedad valor = gson.fromJson(routingContext.getBodyAsString(), ValorHumedad.class);
		//System.out.println(routingContext.get("id_SensorTemp") );
		mySQLPool.query("INSERT INTO ValorHumedad VALUES ("+ String.valueOf(valor.getId_ValorHumedad())+"," + String.valueOf(valor.getId_SensorHumedad()) +","
					+ String.valueOf(valor.getValor())+","+ String.valueOf(valor.getTimestamp())+")").
		execute().onComplete(res -> {
			if (res.succeeded()) {
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(valor));
			}else {
				routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
				.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
			}

			});
		
	}
	
	private void getValorTempBySensorTempID(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM ValorTemp WHERE id_SensorTemp = '" + routingContext.request().getParam("id_SensorTemp") + "'")
		.execute().onComplete(res -> {
				if (res.succeeded()) {	
					RowSet<Row> resultSet = res.result();
					JsonArray result = new JsonArray();
					
					for (Row row : resultSet) {
						result.add(JsonObject.mapFrom(new ValorTemp(row.getInteger("id_ValorTemp"),
								row.getInteger("id_SensorTemp"), row.getFloat("valorTemp"), row.getLong("timestamp"))));
						
					}
					routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					.end(result.encodePrettily());
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
				}
			});
	}
	
	private void getLastValorTempBySensorTempID(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM ValorTemp WHERE id_SensorTemp = '" + routingContext.request().getParam("id_SensorTemp")+"'" + "ORDER BY timestamp DESC LIMIT 1")
		.execute().onComplete(res -> {
				if (res.succeeded()) {	
					RowSet<Row> resultSet = res.result();
					JsonArray result = new JsonArray();
					
					for (Row row : resultSet) {
						result.add(JsonObject.mapFrom(new ValorTemp(row.getInteger("id_ValorTemp"),
								row.getInteger("id_SensorTemp"), row.getFloat("valorTemp"), row.getLong("timestamp"))));
						
					}
					routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					.end(result.encodePrettily());
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
				}
			});
	}
	
	private void addValorTemp(RoutingContext routingContext) {
		ValorTemp valor = gson.fromJson(routingContext.getBodyAsString(), ValorTemp.class);
		//System.out.println(routingContext.get("id_SensorTemp") );
		mySQLPool.query("INSERT INTO ValorTemp VALUES ("+ String.valueOf(valor.getId_ValorTemp())+"," + String.valueOf(valor.getId_SensorTemp()) +","
					+ String.valueOf(valor.getValor())+","+ String.valueOf(valor.getTimestamp())+")").
		execute().onComplete(res -> {
			if (res.succeeded()) {
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(gson.toJson(valor));
			}else {
				routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
				.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
			}

			});
	}
}