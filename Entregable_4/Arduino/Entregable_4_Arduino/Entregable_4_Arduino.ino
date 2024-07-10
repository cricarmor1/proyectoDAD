
#include <RestClient.h>

#include <HTTPClient.h>
#include <Bonezegei_DHT11.h>
#include "ArduinoJson.h"
#include <WiFi.h>


int test_delay = 1000; //so we don't spam the API
boolean describe_tests = true;
WiFiClient client; 
const int DEVICE_ID = 1;
//RestClient client = RestClient("192.168.123.104", 80);
String SERVER_IP = "192.168.0.16";
int SERVER_PORT = 80;
#define STASSID "CASA CARRILLO"
#define STAPSK  "Nglcrr67"
Bonezegei_DHT11 dht(4);

//Setup
void setup()
{
  dht.begin();
  String serverName = "http://192.168.0.16/";
  Serial.begin(9600);
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(STASSID);


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
}
  
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
  delay(10000);
}

void getSensorTempOfPlaca(int id_Placa){
  if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(client, SERVER_IP, SERVER_PORT, "/api/SensorTemp/all/" + String(id_Placa), true);
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
      http.begin(client, SERVER_IP, SERVER_PORT, "/api/ValorTemp/last/" + String(id_SensorTemp), true);
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
      http.begin(client, SERVER_IP, SERVER_PORT, "/api/ValorTemp/add", true);
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

void getSensorHumedadOfPlaca(int id_Placa){
  if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(client, SERVER_IP, SERVER_PORT, "/api/SensorHumedad/all/" + String(id_Placa), true);
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
}
void getLastValorHumedadBySensorHumedad(int id_SensorHumedad){
  if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(client, SERVER_IP, SERVER_PORT, "/api/ValorHumedad/last/" + String(id_SensorHumedad), true);
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
}
String addValorHumedad( int id_SensorHumedad, float valor, long timestamp){
  DynamicJsonDocument doc(2048);

  doc["id_SensorHumedad"] = id_SensorHumedad;
  doc["valor"] = valor;
  doc["timestamp"] = timestamp;
  String output;
  serializeJson(doc,output);

  if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(client, SERVER_IP, SERVER_PORT, "/api/ValorHumedad/add", true);
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

void getActuadorOfPlaca(int id_Placa){
  if (WiFi.status() == WL_CONNECTED){
      HTTPClient http;
      http.begin(client, SERVER_IP, SERVER_PORT, "/api/Actuador/all/" + String(id_Placa), true);
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
      http.begin(client, SERVER_IP, SERVER_PORT, "/api/Estados/last/" + String(id_Actuador), true);
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
      http.begin(client, SERVER_IP, SERVER_PORT, "/api/Estados/add", true);
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
      http.begin(client, SERVER_IP, SERVER_PORT, "/api/Placa/" + String(id_Placa), true);
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