package verticles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
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

public class ApiVerticle extends AbstractVerticle
{
	private Gson gson;
	private Map<Integer, SensorTemp> memoria_SensorTemp;
	private Map<Integer, SensorHumedad> memoria_SensorHumedad;
	private Map<Integer, Actuador> memoria_Actuador;
	private Map<Integer, Grupo> memoria_Grupo;
	private Map<Integer, Placa> memoria_Placa;
	
	private Map<Integer, ArrayList<EstadoActuador>> memoria_Estado;
	private Map<Integer, ArrayList<ValorHumedad>> memoria_ValorHumedad;
	private Map<Integer, ArrayList<ValorTemp>> memoria_ValorTemp;
	
	public void start(Promise<Void> startFuture) {
		EstadoActuador estado1 = new EstadoActuador(1,5,180L,100L);
		EstadoActuador estado2 = new EstadoActuador(2,5,100L,200L);
		EstadoActuador estado3 = new EstadoActuador(3,5,180L,300L);
		EstadoActuador estado4 = new EstadoActuador(4,6,180L,100L);
		EstadoActuador estado5 = new EstadoActuador(5,6,100L,200L);
		EstadoActuador estado6 = new EstadoActuador(6,6,180L,300L);
		
		ValorHumedad valorHumedad1 = new ValorHumedad(1,100,180L,100L);
		ValorHumedad valorHumedad2 = new ValorHumedad(2,100,100L,200L);
		ValorHumedad valorHumedad3 = new ValorHumedad(3,100,180L,300L);
		ValorHumedad valorHumedad4 = new ValorHumedad(4,200,180L,100L);
		ValorHumedad valorHumedad5 = new ValorHumedad(5,200,100L,200L);
		ValorHumedad valorHumedad6 = new ValorHumedad(6,200,180L,300L);
		
		ValorTemp valorTemp1 = new ValorTemp(1,1,180L,100L);
		ValorTemp valorTemp2 = new ValorTemp(2,1,100L,200L);
		ValorTemp valorTemp3 = new ValorTemp(3,1,180L,300L);
		ValorTemp valorTemp4 = new ValorTemp(4,2,180L,100L);
		ValorTemp valorTemp5 = new ValorTemp(5,2,100L,200L);
		ValorTemp valorTemp6 = new ValorTemp(6,2,180L,300L);
		
		SensorTemp sensor1 = new SensorTemp(1,100);
		SensorTemp sensor2 = new SensorTemp(2,200);
		SensorHumedad sensorHum1 = new SensorHumedad(100,1);
		SensorHumedad sensorHum2 = new SensorHumedad(200,2);
		Actuador actuador1 = new Actuador(5,5);
		Actuador actuador2 = new Actuador(6,6);
		Grupo grupo1 = new Grupo(1,1000);
		Grupo grupo2 = new Grupo(2,2000);
		Placa placa1 = new Placa(5, 6, 7);
		Placa placa2 = new Placa(7, 6, 5);
		
		memoria_SensorTemp = new HashMap<Integer, SensorTemp>();
		memoria_SensorHumedad = new HashMap<Integer, SensorHumedad>();
		memoria_Actuador = new HashMap<Integer, Actuador>();
		memoria_Grupo = new HashMap<Integer, Grupo>();
		memoria_Placa = new HashMap<Integer, Placa>();
		memoria_Placa = new HashMap<Integer, Placa>();
		memoria_Estado = new HashMap<Integer, ArrayList<EstadoActuador>>();
		memoria_ValorHumedad = new HashMap<Integer, ArrayList<ValorHumedad>>();
		memoria_ValorTemp = new HashMap<Integer, ArrayList<ValorTemp>>();
		
		ArrayList<EstadoActuador> estados1 = new ArrayList<EstadoActuador>();
		ArrayList<EstadoActuador> estados2 = new ArrayList<EstadoActuador>();
		estados1.add(estado1);
		estados1.add(estado2);
		estados1.add(estado3);
		estados2.add(estado4);
		estados2.add(estado5);
		estados2.add(estado6);
		ArrayList<ValorHumedad> valoresHumedad1 = new ArrayList<ValorHumedad>();
		ArrayList<ValorHumedad> valoresHumedad2 = new ArrayList<ValorHumedad>();
		valoresHumedad1.add(valorHumedad1);
		valoresHumedad1.add(valorHumedad2);
		valoresHumedad1.add(valorHumedad3);
		valoresHumedad2.add(valorHumedad4);
		valoresHumedad2.add(valorHumedad5);
		valoresHumedad2.add(valorHumedad6);
		ArrayList<ValorTemp> valoresTemp1 = new ArrayList<ValorTemp>();
		ArrayList<ValorTemp> valoresTemp2 = new ArrayList<ValorTemp>();
		valoresTemp1.add(valorTemp1);
		valoresTemp1.add(valorTemp2);
		valoresTemp1.add(valorTemp3);
		valoresTemp2.add(valorTemp4);
		valoresTemp2.add(valorTemp5);
		valoresTemp2.add(valorTemp6);

		memoria_SensorTemp.put(1, sensor1);
		memoria_SensorTemp.put(2, sensor2);
		memoria_SensorHumedad.put(100, sensorHum1);
		memoria_SensorHumedad.put(200, sensorHum2);
		memoria_Actuador.put(5, actuador1);
		memoria_Actuador.put(6, actuador2);
		memoria_Grupo.put(1, grupo1);
		memoria_Grupo.put(2, grupo2);
		memoria_Placa.put(5, placa1);
		memoria_Placa.put(7, placa2);
		memoria_Estado.put(5, estados1);
		memoria_Estado.put(6, estados2);
		memoria_ValorHumedad.put(100, valoresHumedad1);
		memoria_ValorHumedad.put(200, valoresHumedad2);
		memoria_ValorTemp.put(1, valoresTemp1);
		memoria_ValorTemp.put(2, valoresTemp2);
	
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
		router.post("/api/ValorTemp/add").handler(this::addValorTemp);
	}
	
	private void getSensorTempByID(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(memoria_SensorTemp.get(Integer.parseInt(routingContext.request().getParam("id_SensorTemp")))));
	}
	
	private void addSensorTemp(RoutingContext routingContext) {
		SensorTemp sensor = gson.fromJson(routingContext.getBodyAsString(), SensorTemp.class);
		memoria_SensorTemp.put(sensor.getId_SensorTemp(), sensor);
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(memoria_SensorTemp.get(sensor.getId_SensorTemp())));
	}
	
	private void getSensorHumedadByID(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(memoria_SensorHumedad.get(Integer.parseInt(routingContext.request().getParam("id_SensorHumedad")))));
	}
	
	private void addSensorHumedad(RoutingContext routingContext) {
		SensorHumedad sensor = gson.fromJson(routingContext.getBodyAsString(), SensorHumedad.class);
		memoria_SensorHumedad.put(sensor.getSensorhumedad_id(), sensor);
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(memoria_SensorHumedad.get(sensor.getSensorhumedad_id())));
	}
	
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
	}
}