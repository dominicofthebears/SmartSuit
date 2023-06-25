package SmartSuit.unipi.it.remoteControlApplication;

import SmartSuit.unipi.it.DatabaseAccess;

import java.sql.SQLException;
import java.util.HashMap;

public class PeriodicDataRetrieval implements Runnable{ //singleton class


    private static HashMap<String, Integer> thresholds = new HashMap<>();
    private static final PeriodicDataRetrieval instance = new PeriodicDataRetrieval();

    private PeriodicDataRetrieval(){
        thresholds.put("radiation", 100); //millisievert
        thresholds.put("gas", 35); //PPM
        thresholds.put("electromagnetic", 40); //V/m
    }

    public static PeriodicDataRetrieval getInstance(){
        return instance;
    }

    public void run() {

        try{
            HashMap<String, Integer> values = DatabaseAccess.retrieveData();
            //System.out.println(values);
            if (values.isEmpty()){
                System.out.println("Could not retrieve any data");
            }
            else{
                for(String key : values.keySet()){
                    HashMap<String, String> act = DatabaseAccess.retrieveActuator(key);

                    if(values.get(key) > thresholds.get(key)){

                        CoAP_Client.setIsDanger(key, true);
                        if(act.get("status").equals("OFF")){
                            System.err.println("Danger detected on " + key + " sensor, advertising the actuator");
                            CoAP_Client.actuatorCall(act.get("ip"), key, "ON", 1);
                        }
                    }
                    else{

                        CoAP_Client.setIsDanger(key, false); //turning off the actuator if under 30% of the threshold
                        if (values.get(key) < thresholds.get(key)*0.3 && act.get("status").equals("ON")) {
                            System.err.println("Turning off the " + key + " actuator since there is no danger");
                            CoAP_Client.actuatorCall(act.get("ip"), key, "OFF", 0);
                        }
                    }
                }
            }
        }catch (SQLException e){
            //System.out.println(e);
            System.err.println("Database error");
        }finally {

            Thread.currentThread().interrupt();

        }
    }

    public static void setRadiationThreshold(Integer val){
        thresholds.put("radiation", val);
    }

    public static void setGasThreshold(Integer val){
        thresholds.put("gas", val);
    }

    public static void setElectromagneticThreshold(Integer val){
        thresholds.put("electromagnetic", val);
    }

}
