package verticles;

import java.util.Map;

import com.google.gson.Gson;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import types.ValorTemp;
import types.Grupo;
import types.SensorTemp;
import types.ValorHumedad;

public class MqttVerticle extends AbstractVerticle {
	
	Gson gson;
	private WebClient client;
	public void start(Promise<Void> startFuture) {
		client = WebClient.create(vertx);
			
		gson = new Gson();
		MqttClient mqttClient = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true));
		mqttClient.connect(1883, "localhost", s -> {
			
			//handleTemp("2", mqttClient);
			
			suscribirGrupos(mqttClient);
			
			mqttClient.subscribe("grupo", MqttQoS.AT_LEAST_ONCE.value(), handlerTemp -> {
				if (handlerTemp.succeeded()) {
					System.out.println("Suscripción " + mqttClient.clientId());
				}
			});
			
			
			
			mqttClient.publishHandler(handler -> {
				System.out.println("Mensaje recibido:");
				System.out.println("    Topic: " + handler.topicName().toString());
				
				if(handler.topicName().toString().equals("grupo")) {
					if(handler.payload().toString().equals("Nuevo")) {
						suscribirGrupos(mqttClient);
					}
				}
				else {
					
					String trozos[] = handler.topicName().toString().split("/");
					String grupo = trozos[0];
					String tipo = trozos[1];
				
					if(tipo.equals("temperatura")) {
						if(handler.payload().toString().equals("NuevaTemp")) {
							System.out.println("TEMPERATURA CORRECTO");
							System.out.println(grupo);
							handleTemp(grupo,mqttClient);
						}
					}
					/*else if(tipo.equals("humedad")) {
						if(handler.payload().toString().equals("NuevaHum")) {
							System.out.println("HUMEDAD CORRECTO");
						}
					}*/
				
				}
				
				
			});
			
		
			
			//mqttClient.publish("topic_1", Buffer.buffer("HOLAAA"), MqttQoS.AT_LEAST_ONCE, false, false);
		});

	}
	
	private void suscribirGrupos(MqttClient mqttClient) {
		
		client = WebClient.create(vertx);
		
		client
		  .get(80, "127.0.0.1", "/api/Grupo/all/1")
		  .send()
		  .onSuccess(response ->{
			  System.out.println("dentro");
			  System.out.println(response.bodyAsString());
			  Grupo[] enums = gson.fromJson(response.bodyAsString(), Grupo[].class);
			  for(Grupo g:enums) {
				  mqttClient.subscribe(g.getMqtt_ch() + "/temperatura", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
						if (handler.succeeded()) {
							System.out.println("Suscripción " + mqttClient.clientId());
						}
					});
				 /* mqttClient.subscribe(g.getMqtt_ch() + "/humedad", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
						if (handler.succeeded()) {
							System.out.println("Suscripción " + mqttClient.clientId());
						}
					});*/
		  }})
		  .onFailure(err ->
		    System.out.println("Something went wrong " + err.getMessage()));
	}
	
	
	
	public <T> void getRequest(Integer port, String host, String resource, Class<T> classType, Promise<T> promise) {
		client.getAbs(host + ":" + port + "/" + resource).send(elem -> {
			if (elem.succeeded()) {
				promise.complete(gson.fromJson(elem.result().bodyAsString(), classType));
			} else {
				promise.fail(elem.cause());
			}
		});

	}
	
	public <B, T> void putRequest(Integer port, String host, String resource, Object body, Class<T> classType,
			Promise<T> promise) {
		JsonObject jsonBody = new JsonObject(gson.toJson(body));
		client.putAbs(host + ":" + port + "/" + resource).sendJsonObject(jsonBody, elem -> {
			if (elem.succeeded()) {
				Gson gson = new Gson();
				promise.complete(gson.fromJson(elem.result().bodyAsString(), classType));
			} else {
				promise.fail(elem.cause());
			}
		});
	}
	
	public void handleTemp( String mqtt_ch, MqttClient mqttclient) {
		client = WebClient.create(vertx);
		Promise<Grupo[]> resList = Promise.promise();
		resList.future().onComplete(complete -> {
			System.out.println("-----------------------------------------------------------");
			if (complete.succeeded()) {
				System.out.println("Resource list obtained");
				System.out.println(complete.result()[0].getMqtt_ch() );
				if (complete.result()[0].getTimestampTemp() != null) {
			       
					Promise<ValorTemp[]> resTemp = Promise.promise();
					resTemp.future().onComplete(complete2 ->{
					for(ValorTemp v:complete2.result()) {
						System.out.println(v.getTimestamp());
					}
						Promise<ValorTemp[]> resTemp2 = Promise.promise();
						resTemp2.future().onComplete(complete3 ->{
							float resultadofinal = complete3.result()[0].getValor() - complete2.result()[0].getValor();
							if (resultadofinal >= 0.2) {
								mqttclient.publish(mqtt_ch + "/servo", Buffer.buffer("180"), MqttQoS.AT_LEAST_ONCE, false, false);
								System.out.println("FUNCIONANDO");
							}else {
								mqttclient.publish(mqtt_ch + "/servo", Buffer.buffer("0"), MqttQoS.AT_LEAST_ONCE, false, false);
							}
							System.out.println("RESULTADO DE LA RESTA: " + resultadofinal);
							Promise<Grupo> resPut = Promise.promise();
							putRequest(80, "http://localhost", "api/Grupo/update/"+mqtt_ch, new Grupo(complete.result()[0].getId_Grupo(), 
									complete.result()[0].getMqtt_ch(), complete3.result()[0].getTimestamp(), complete.result()[0].getTimestampHum()), Grupo.class, resPut);
					});
						getRequest(80, "http://localhost", "api/Grupo/lasttemp/"+mqtt_ch , ValorTemp[].class, resTemp2);
					});
					String res = complete.result()[0].getTimestampTemp().toString();
					System.out.println(res);
					getRequest(80, "http://localhost", "api/Grupo/lasttemp/"+mqtt_ch+"/"+ res , ValorTemp[].class, resTemp);
				} else {
					Promise<ValorTemp[]> resTemp2 = Promise.promise();
					resTemp2.future().onComplete(complete3 ->{
						Promise<Grupo> resPut = Promise.promise();
						System.out.println(complete3.result()[0].getId_ValorTemp() );
						putRequest(80, "http://localhost", "api/Grupo/update/"+mqtt_ch, new Grupo(complete.result()[0].getId_Grupo(), 
								complete.result()[0].getMqtt_ch(), complete3.result()[0].getTimestamp(), complete.result()[0].getTimestampHum()), Grupo.class, resPut);
					});
					getRequest(80, "http://localhost", "api/Grupo/lasttemp/"+mqtt_ch , ValorTemp[].class, resTemp2);
				}
			} else {
				System.out.println("Resource list not obtained");
				System.out.println(complete.cause().toString());
			}
		}).onSuccess(success -> {
			 System.out.println(success.toString());
		}).onFailure(failure -> {
			// System.out.println(failure.toString());
		});
		
		
		getRequest(80, "http://localhost", "/api/Grupo/mqtt/"+mqtt_ch, Grupo[].class, resList);
		
	}
	
	//////HASTA AQUI FURULA

}

	
