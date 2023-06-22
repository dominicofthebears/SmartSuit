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
            if (values.isEmpty()){
                System.out.println("Could not retrieve any data");
            }
            else{
                for(String key : values.keySet()){
                    if(values.get(key) > thresholds.get(key)){
                        CoAP_Client.actuatorCall(DatabaseAccess.retrieveActuator(key), key, "ON", 1);
                    }
                }
            }
        }catch (SQLException e){
            System.out.println("Database error");
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
