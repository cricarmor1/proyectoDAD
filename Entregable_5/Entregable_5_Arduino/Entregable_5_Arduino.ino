
#include <RestClient.h>
#include <HTTPClient.h>
#include <Bonezegei_DHT11.h>
#include "ArduinoJson.h"
#include <WiFi.h>
#include <PubSubClient.h>
#include <ESP32Servo.h>

//Información relativa a la placa
const int Placa_id = 155;
int Grupo_id;
String grupo = "laboratorio_1";
char prueba[20] = "'laboratorio_1'";
char topic1[50] = "laboratorio_1/temperatura";
char topic3[50] = "laboratorio_1/servo";

//Información relativa a sensores y actuadores
const int numeroSensoresTemp = 2;
const int numeroActuadores = 4;
int sensorTemp[numeroSensoresTemp];
char msgTemp[2048];
int actuadores[numeroActuadores];
int ledPin[numeroActuadores] = {14,15,16,17};
Bonezegei_DHT11 dht(4);
Servo miServo;
const int pinServo = 18;
unsigned long tiempo_loop_sensores = 0;

//const int numeroSensoresHum = 2;
//char topic2[50] = "laboratorio_1/humedad";
//int sensorHum[numeroSensoresHum];
//char msgHum[2048];

//Información relativa a conexión
WiFiClient clientHTTP;
WiFiClient clientMQTT; 
String SERVER_IP = "192.168.0.16";
int SERVER_PORT = 80;
RestClient restclient = RestClient("192.168.123.104", 80);
#define STASSID "CASA CARRILLO"
#define STAPSK  "Nglcrr67"
PubSubClient MQTTClient(clientMQTT);


//Setup
void setup()
{
  dht.begin();
  miServo.attach(pinServo);
  String serverName = "http://192.168.0.16/";
  Serial.begin(9600);
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(STASSID);
  for(int i = 0; i<numeroActuadores; i++){
    pinMode(ledPin[i], OUTPUT);
    digitalWrite(ledPin[i], LOW);
  }


  /* Explicitly set the ESP8266 to be a WiFi-client, otherwise, it by default,
     would try to act as both a client and an access-point and could cause
     network-issues with your other WiFi-devices on your WiFi-network. */
  WiFi.mode(WIFI_STA);
  WiFi.begin(STASSID, STAPSK);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
  Serial.println("Setup!");

  presentacion(Placa_id);
  presentacionSensorTemp(Placa_id);
  //presentacionSensorHum(Placa_id);
  Serial.println(String(sensorTemp[0]));
  Serial.println(String(sensorTemp[1]));
  //Serial.println(String(sensorHum[0]));
  //Serial.println(String(sensorHum[1]));
  presentacionActuadores(Placa_id);
  Serial.println(String(actuadores[0]));
  Serial.println(String(actuadores[1]));

  MQTTClient.setServer("192.168.0.16", 1883);
  MQTTClient.setCallback(callback);

}
 /* 
void loop(){
  float tempDeg;
  float hum;
  getPlacaById(1);
  //getSensorTempOfPlaca(1);
  //getLastValorTempBySensorTemp(1);
  //getSensorHumedadOfPlaca(1);
  //getLastValorHumedadBySensorHumedad(1000);
  /*if(dht.getData()){
    tempDeg = dht.getTemperature();
    hum = dht.getHumidity();
    Serial.println(tempDeg);
  }*/
  //String añadido = addValorTemp(1,tempDeg,millis());
  //String añadido = addValorHumedad(1000, hum, millis());
  //Serial.println(añadido);
  //getActuadorOfPlaca(1);
  //getLastEstadoActuadorByActuador(1000);
  //String añadido = addEstado(1000, 500,millis());
  //Serial.println(añadido);
  /*
  delay(10000);
}*/

void loop() {
 
  if (!MQTTClient.connected()) {
    reconnect();
  }
  miServo.write(0);
  MQTTClient.loop();
  //miServo.write(0);
  long now = millis();
  if (now - tiempo_loop_sensores > 5000) {
    tiempo_loop_sensores = now;

    float tempDeg;
    //float hum;
   // miServo.write(0);

    if(dht.getData()){
    tempDeg = dht.getTemperature();
    //hum = dht.getHumidity();
  }
   String añadidoTemp = addValorTemp(sensorTemp[0],tempDeg,millis());
   //String añadidoHum = addValorHumedad(sensorHum[0], hum, millis());
  //Serial.println(añadido);
    Serial.print("Publish message: ");
    Serial.println(tempDeg);
    //Serial.println(hum);
    añadidoTemp.toCharArray(msgTemp,2048);
    //añadidoHum.toCharArray(msgHum,2048);
    MQTTClient.publish("laboratorio_1/temperatura", "NuevaTemp");
    //MQTTClient.publish("laboratorio_1/humedad", "NuevaHum");
  }
}

void callback(char* topic, byte* payload, unsigned int length) {
  String mensajeRecibido;
  Serial.print("Message arrived [");
  for (int i = 0; i < length; i++) {
    mensajeRecibido = mensajeRecibido + (char)payload[i];
  }
  Serial.print("] ");
  Serial.println(mensajeRecibido);
  if(String(topic) == grupo + "/servo"){
    //Serial.println("DENTROOOOOOOOO");
    miServo.write(mensajeRecibido.toInt());
    if(mensajeRecibido.toInt() == 180){
      for(int i = 0; i<numeroActuadores;i++){
        digitalWrite(ledPin[i], HIGH);
      
        String añadidoEstado = addEstado(actuadores[i],mensajeRecibido.toInt(),millis());
    }}
    else if(mensajeRecibido.toInt() == 0){
      for(int i = 0; i<numeroActuadores;i++){
        digitalWrite(ledPin[i], LOW);
      
        String añadidoEstado = addEstado(actuadores[i],mensajeRecibido.toInt(),millis());
    }
  }
}
}
 
void reconnect() {
  while (!MQTTClient.connected()) {
    Serial.print("Attempting MQTT connection...");
    if (MQTTClient.connect("ESP8266Client")) {
      Serial.println("connected");
      //MQTTClient.publish("casa/despacho/temperatura", "Enviando el primer mensaje");
      //char grupo1[20];
      //grupo.toCharArray(grupo1, 20);
      MQTTClient.publish("grupo", "Nuevo");
      MQTTClient.subscribe(topic3);
    } else {
      Serial.print("failed, rc=");
      Serial.print(MQTTClient.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}


void getSensorTempOfPlaca(int id_Placa){
  if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/SensorTemp/all/" + String(id_Placa), true);
      if(http.GET() == 200){
        String payload = http.getString();
        DynamicJsonDocument doc(2048);
        deserializeJson(doc, payload);
        
        JsonArray array = doc.as<JsonArray>();
        for(JsonObject v : array) {
          int id_SensorTemp = v["id_SensorTemp"].as<int>();
          int id_Placa = v["id_Placa"].as<int>();
          Serial.println(String(id_SensorTemp));
          Serial.println(String(id_Placa, DEC));
          }
      
        
      }else Serial.println("Error al conectar con el servidor");
      
    }else Serial.println("Error al conectar mediante WIFI");
}
void getLastValorTempBySensorTemp(int id_SensorTemp){
  if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/ValorTemp/last/" + String(id_SensorTemp), true);
      if(http.GET() == 200){
        String payload = http.getString();
        DynamicJsonDocument doc(2048);
        deserializeJson(doc, payload);
        
        JsonArray array = doc.as<JsonArray>();
        for(JsonObject v : array) {
          int id_ValorTemp = v["id_ValorTemp"].as<int>();
          int id_SensorTemp = v["id_SensorTemp"].as<int>();
          float valor = v["valor"].as<float>();
          long timestamp = v["timestamp"].as<long>();
          Serial.println(String(id_ValorTemp));
          Serial.println(String(id_SensorTemp, DEC));
          Serial.println(String(valor, DEC));
          Serial.println(String(timestamp, DEC));
          }
      
        
      }else Serial.println("Error al conectar con el servidor");
      
    }else Serial.println("Error al conectar mediante WIFI");
}
String addValorTemp( int id_SensorTemp, float valor, long timestamp){
  DynamicJsonDocument doc(2048);

  doc["id_SensorTemp"] = id_SensorTemp;
  doc["valor"] = valor;
  doc["timestamp"] = timestamp;
  String output;
  serializeJson(doc,output);

  if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/ValorTemp/add", true);
      int status = http.POST(output);
      if(status == 200){
        Serial.println("Todo correcto!!");
      }
      else{
        Serial.println("Muy mal");
      }
      
    }else Serial.println("Error al conectar mediante WIFI");

  return output;
}

/*void getSensorHumedadOfPlaca(int id_Placa){
  if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/SensorHumedad/all/" + String(id_Placa), true);
      if(http.GET() == 200){
        String payload = http.getString();
        DynamicJsonDocument doc(2048);
        deserializeJson(doc, payload);
        
        JsonArray array = doc.as<JsonArray>();
        for(JsonObject v : array) {
          int id_SensorHumedad = v["id_SensorHumedad"].as<int>();
          int id_Placa = v["id_Placa"].as<int>();
          Serial.println(String(id_SensorHumedad));
          Serial.println(String(id_Placa, DEC));
          }
      
        
      }else Serial.println("Error al conectar con el servidor");
      
    }else Serial.println("Error al conectar mediante WIFI");
}*/
/*void getLastValorHumedadBySensorHumedad(int id_SensorHumedad){
  if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/ValorHumedad/last/" + String(id_SensorHumedad), true);
      if(http.GET() == 200){
        String payload = http.getString();
        DynamicJsonDocument doc(2048);
        deserializeJson(doc, payload);
        
        JsonArray array = doc.as<JsonArray>();
        for(JsonObject v : array) {
          int id_ValorHumedad = v["id_ValorHumedad"].as<int>();
          int id_SensorHumedad = v["id_SensorHumedad"].as<int>();
          float valor = v["valor"].as<float>();
          long timestamp = v["timestamp"].as<long>();
          Serial.println(String(id_ValorHumedad));
          Serial.println(String(id_SensorHumedad, DEC));
          Serial.println(String(valor, DEC));
          Serial.println(String(timestamp, DEC));
          }
      
        
      }else Serial.println("Error al conectar con el servidor");
      
    }else Serial.println("Error al conectar mediante WIFI");
}*/
/*String addValorHumedad( int id_SensorHumedad, float valor, long timestamp){
  DynamicJsonDocument doc(2048);

  doc["id_SensorHumedad"] = id_SensorHumedad;
  doc["valor"] = valor;
  doc["timestamp"] = timestamp;
  String output;
  serializeJson(doc,output);

  if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/ValorHumedad/add", true);
      int status = http.POST(output);
      if(status == 200){
        Serial.println("Todo correcto!!");
      }
      else{
        Serial.println("APAÑATELAS");
      }
      
    }else Serial.println("Error al conectar mediante WIFI");

  return output;
}*/

void getActuadorOfPlaca(int id_Placa){
  if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/Actuador/all/" + String(id_Placa), true);
      if(http.GET() == 200){
        String payload = http.getString();
        DynamicJsonDocument doc(2048);
        deserializeJson(doc, payload);
        
        JsonArray array = doc.as<JsonArray>();
        for(JsonObject v : array) {
          int id_Actuador = v["id_Actuador"].as<int>();
          int id_Placa = v["id_Placa"].as<int>();
          Serial.println(String(id_Actuador));
          Serial.println(String(id_Placa, DEC));
          }
      
        
      }else Serial.println("Error al conectar con el servidor");
      
    }else Serial.println("Error al conectar mediante WIFI");
}
void getLastEstadoActuadorByActuador(int id_Actuador){
  if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/Estados/last/" + String(id_Actuador), true);
      if(http.GET() == 200){
        String payload = http.getString();
        DynamicJsonDocument doc(2048);
        deserializeJson(doc, payload);
        
        JsonArray array = doc.as<JsonArray>();
        for(JsonObject v : array) {
          int id_EstadoActuador = v["id_EstadoActuador"].as<int>();
          int id_Actuador = v["id_Actuador"].as<int>();
          float estado = v["estado"].as<float>();
          long timestamp = v["timestamp"].as<long>();
          Serial.println(String(id_EstadoActuador));
          Serial.println(String(id_Actuador, DEC));
          Serial.println(String(estado, DEC));
          Serial.println(String(timestamp, DEC));
          }
      
        
      }else Serial.println("Error al conectar con el servidor");
      
    }else Serial.println("Error al conectar mediante WIFI");
}
String addEstado( int id_Actuador, float estado, long timestamp){
  DynamicJsonDocument doc(2048);

  doc["id_Actuador"] = id_Actuador;
  doc["estado"] = estado;
  doc["timestamp"] = timestamp;
  String output;
  serializeJson(doc,output);

  if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/Estados/add", true);
      int status = http.POST(output);
      if(status == 200){
        Serial.println("Todo correcto!!");
      }
      else{
        Serial.println("Muy mal");
      }
      
    }else {Serial.println("Error al conectar mediante WIFI");}

  return output;
}

void getPlacaById(int id_Placa){
  if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/Placa/" + String(id_Placa), true);
      if(http.GET() == 200){
        String payload = http.getString();
        DynamicJsonDocument doc(2048);
        deserializeJson(doc, payload);
        
        JsonArray array = doc.as<JsonArray>();
        for(JsonObject v : array) {
          int id_Placa = v["id_Placa"].as<int>();
          int id_Grupo = v["id_Grupo"].as<int>();
          int mqtt_ch = v["mqtt_ch"].as<int>();
          Serial.println(String(id_Placa));
          Serial.println(String(id_Grupo));
          Serial.println(String(mqtt_ch));
          }
      
        
      }else Serial.println("Error al conectar con el servidor");
      
    }else Serial.println("Error al conectar mediante WIFI");
}

void addPlaca( int id_Placa, int id_Grupo, char mqtt_ch[]){
  DynamicJsonDocument doc(2048);

  doc["id_Placa"] = id_Placa;
  doc["id_Grupo"] = id_Grupo;
  doc["mqtt_ch"] = mqtt_ch;
  String output;
  serializeJson(doc,output);

  if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/Placa/add", true);
      int status = http.POST(output);
      if(status == 200){
        Serial.println("Todo correcto!!");
      }
      else{
        Serial.println("Muy mal");
      }
      
    }else {Serial.println("Error al conectar mediante WIFI");}

}
int getGrupoByMqtt(String mqtt_ch){
  int res = 0;
  if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/Grupo/mqtt/" + mqtt_ch, true);
      if(http.GET() == 200){
        String payload = http.getString();
        DynamicJsonDocument doc(2048);
        deserializeJson(doc, payload);
        
        JsonArray array = doc.as<JsonArray>();
        for(JsonObject v : array) {
          res = v["id_Grupo"].as<int>();
          }
      
        
      }else Serial.println("Error al conectar con el servidor");
      
    }else Serial.println("Error al conectar mediante WIFI");
  return res;
}

void addGrupo(char mqtt_ch[]){
  DynamicJsonDocument doc(2048);

  doc["mqtt_ch"] = mqtt_ch;
  String output;
  serializeJson(doc,output);

  if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/Grupo/add", true);
      int status = http.POST(output);
      Serial.println(String(status));
      if(status == 200){
        Serial.println("Todo correcto!!");
      }
      else{
        Serial.println("LO SIENTO");
      }
      
    }else {Serial.println("Error al conectar mediante WIFI");}

}

void presentacion(int id_Placa){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/Placa/" + String(id_Placa), true);
    //Serial.print("HOLAAAA");
    if(http.GET() == 200){
      String payload = http.getString();
      Serial.println(payload);
      
      if(payload == "[ ]"){
        
        http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/Grupo/mqtt/" + grupo, true);
        if(http.GET() == 200){
          payload = http.getString();
          if(payload == "[ ]"){
            //Serial.println("HOLAAAAAAAAAAAAA");
            addGrupo(prueba);
            Grupo_id = getGrupoByMqtt(grupo);
            Serial.println(String(Grupo_id));
            addPlaca(Placa_id, Grupo_id, prueba);
            MQTTClient.publish("grupo", "Nuevo");
          }
          else{
            Grupo_id = getGrupoByMqtt(grupo);
            Serial.println(String(Grupo_id));
            addPlaca(Placa_id, Grupo_id, prueba);
          }
        }
      }
      /*else{
        DynamicJsonDocument doc(2048);
        deserializeJson(doc, payload);
        
        JsonArray array = doc.as<JsonArray>();
        for(JsonObject v : array) {
          Grupo_id = v["id_Grupo"].as<int>();
      
        }
    }*/
  }
  }
}

void presentacionSensorTemp(int id_Placa){
  if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/SensorTemp/all/" + String(id_Placa), true);
      if(http.GET() == 200){
        String payload = http.getString();
        if(payload == "[ ]"){

          for(int i = 0; i<numeroSensoresTemp;i++){
          DynamicJsonDocument doc(2048);
          doc["id_Placa"] = id_Placa;
          String output;
          serializeJson(doc,output);
          http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/SensorTemp/add", true);
          int status = http.POST(output);
          }

          http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/SensorTemp/all/" + String(id_Placa), true);
          if(http.GET()== 200){

            int contador = 0;
            String payload = http.getString();
            DynamicJsonDocument doc(2048);
            deserializeJson(doc, payload);
        
            JsonArray array = doc.as<JsonArray>();
            for(JsonObject v : array) {
            int id_SensorTemp = v["id_SensorTemp"].as<int>();
            int id_Placa = v["id_Placa"].as<int>();
            sensorTemp[contador] = id_SensorTemp;
            contador++;
          }
          }
        }else{


        DynamicJsonDocument doc(2048);
        deserializeJson(doc, payload);
        int contador = 0;
        JsonArray array = doc.as<JsonArray>();
        for(JsonObject v : array) {
          int id_SensorTemp = v["id_SensorTemp"].as<int>();
          int id_Placa = v["id_Placa"].as<int>();
          sensorTemp[contador] = id_SensorTemp;
          contador++;
        
          }
        }
      
        
      }else Serial.println("Error al conectar con el servidor");
      
    }else Serial.println("Error al conectar mediante WIFI");
}

/*
void presentacionSensorHum(int id_Placa){
  if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/SensorHumedad/all/" + String(id_Placa), true);
      if(http.GET() == 200){
        String payload = http.getString();
        if(payload == "[ ]"){

          for(int i = 0; i<numeroSensoresHum;i++){
          DynamicJsonDocument doc(2048);
          doc["id_Placa"] = id_Placa;
          String output;
          serializeJson(doc,output);
          http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/SensorHumedad/add", true);
          int status = http.POST(output);
          }

          http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/SensorHumedad/all/" + String(id_Placa), true);
          if(http.GET()== 200){

            int contador = 0;
            String payload = http.getString();
            DynamicJsonDocument doc(2048);
            deserializeJson(doc, payload);
        
            JsonArray array = doc.as<JsonArray>();
            for(JsonObject v : array) {
            int id_SensorHumedad = v["id_SensorHumedad"].as<int>();
            int id_Placa = v["id_Placa"].as<int>();
            sensorHum[contador] = id_SensorHumedad;
            contador++;
          }
          }
        }else{


        DynamicJsonDocument doc(2048);
        deserializeJson(doc, payload);
        int contador = 0;
        JsonArray array = doc.as<JsonArray>();
        for(JsonObject v : array) {
          int id_SensorHumedad = v["id_SensorHumedad"].as<int>();
          int id_Placa = v["id_Placa"].as<int>();
          sensorHum[contador] = id_SensorHumedad;
          contador++;
        
          }
        }
      
        
      }else Serial.println("Error al conectar con el servidor");
      
    }else Serial.println("Error al conectar mediante WIFI");
}*/

void presentacionActuadores(int id_Placa){
  if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/Actuador/all/" + String(id_Placa), true);
      if(http.GET() == 200){
        String payload = http.getString();
        if(payload == "[ ]"){

          for(int i = 0; i<numeroActuadores;i++){
          DynamicJsonDocument doc(2048);
          doc["id_Placa"] = id_Placa;
          String output;
          serializeJson(doc,output);
          http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/Actuador/add", true);
          int status = http.POST(output);
          }

          http.begin(clientHTTP, SERVER_IP, SERVER_PORT, "/api/Actuador/all/" + String(id_Placa), true);
          if(http.GET()== 200){

            int contador = 0;
            String payload = http.getString();
            DynamicJsonDocument doc(2048);
            deserializeJson(doc, payload);
        
            JsonArray array = doc.as<JsonArray>();
            for(JsonObject v : array) {
            int id_Actuador = v["id_Actuador"].as<int>();
            int id_Placa = v["id_Placa"].as<int>();
            actuadores[contador] = id_Actuador;
            contador++;
          }
          }
        }else{


        DynamicJsonDocument doc(2048);
        deserializeJson(doc, payload);
        int contador = 0;
        JsonArray array = doc.as<JsonArray>();
        for(JsonObject v : array) {
          int id_Actuador = v["id_Actuador"].as<int>();
          int id_Placa = v["id_Placa"].as<int>();
          actuadores[contador] = id_Actuador;
          contador++;
        
          }
        }
      
        
      }else Serial.println("Error al conectar con el servidor");
      
    }else Serial.println("Error al conectar mediante WIFI");
}

