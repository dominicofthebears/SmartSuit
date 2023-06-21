package SmartSuit.unipi.it.cloudApplication;

import org.eclipse.paho.client.mqttv3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;

public class MQTT_Collector implements MqttCallback {

    LinkedList<String> topic = new LinkedList<>(Arrays.asList("radiation", "gas", "electromagnetic"));;
    String broker = "tcp://127.0.0.1:1883";
    String clientId = "MQTT_Collector";

    public MQTT_Collector() {
        try{
            MqttClient mqttClient = new MqttClient (broker,clientId);
            mqttClient.setCallback(this);
            mqttClient.connect();
            for (String t : topic){
                mqttClient.subscribe(t);
            }
        } catch (MqttException e){
            System.out.println("Could not connect to the publisher");
        }

    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Connection lost with the queues");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        JSONObject obj;
        JSONParser parser = new JSONParser();

        try {
            obj = (JSONObject) parser.parse(String.valueOf(mqttMessage.getPayload()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        try{
            int modified = SQLConnector.insertData((Float) obj.get("value"), topic);
            if (modified < 1){
                System.err.println("DataBase error: could not insert new data");
            }
        }catch (SQLException e){
            System.err.println("DataBase error: cannot connect");
        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    public static void main(String[] args) throws MqttException {
        new MQTT_Collector();
    }
}
