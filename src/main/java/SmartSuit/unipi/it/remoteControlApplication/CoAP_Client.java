package SmartSuit.unipi.it.remoteControlApplication;

import SmartSuit.unipi.it.DatabaseAccess;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.HashMap;

public class CoAP_Client {

    private static HashMap<String, Boolean> isDanger = new HashMap<>();

    static{
        isDanger.put("radiation", false);
        isDanger.put("gas", false);
        isDanger.put("electromagnetic", false);
    }

    public static void actuatorCall(String ip, String resource, String action, int overThreshold) throws SQLException {

        if(isDanger.get(resource) && action.equals("OFF")){
            System.out.println("There is a danger, you cannot turn off the actuator");
            return;
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
                    System.out.println("State correctly changed because of danger or user input");
                    DatabaseAccess.updateActuators(ip, resource, action);
                    break;
                case BAD_OPTION:
                    System.err.println("Parameters error");
                    break;
            }

        }
    }

    public static void setIsDanger(String danger, boolean val){
        isDanger.put(danger, val);
    }

}

