package verticles;

import com.google.gson.Gson;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;

public class MqttVerticle extends AbstractVerticle {
	
	Gson gson;

	public void start(Promise<Void> startFuture) {
		gson = new Gson();
		MqttClient mqttClient = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true));
		mqttClient.connect(1883, "localhost", s -> {

			mqttClient.subscribe("topic_2", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
				if (handler.succeeded()) {
					System.out.println("Suscripción " + mqttClient.clientId());
				}
			});

			mqttClient.publishHandler(handler -> {
				System.out.println("Mensaje recibido:");
				System.out.println("    Topic: " + handler.topicName().toString());
				System.out.println("    Id del mensaje: " + handler.messageId());
				System.out.println("    Contenido: " + handler.payload().toString());
			});
			mqttClient.publish("topic_1", Buffer.buffer("EjemploJASBDAJSH"), MqttQoS.AT_LEAST_ONCE, false, false);
		});

	}

}
