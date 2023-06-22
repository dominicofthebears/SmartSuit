package SmartSuit.unipi.it.remoteControlApplication;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.json.simple.JSONObject;

public class CoAP_Client {

    public static void actuatorCall(String ip, String resource, String action, boolean overThreshold) {

        if(action == "OFF" && overThreshold){
            System.out.println("Cannot turn off the actuator: there's a danger situation");
        }

        CoapClient client = new CoapClient("coap://" + ip + "/" + resource);

        JSONObject object = new JSONObject();
        object.put("threshold", overThreshold);
        object.put("action", action);

        CoapResponse response = client.put(object.toJSONString(), MediaTypeRegistry.APPLICATION_JSON);

        if (response == null) {
            System.out.println("An error occurred while contacting the actuator");
        } else {
            CoAP.ResponseCode code = response.getCode();
            switch (code) {
                case CHANGED:
                    System.out.println("State correctly changed");
                    break;
                case BAD_OPTION:
                    System.err.println("The actuator is already in this status");
                    break;
            }

        }
    }
}

