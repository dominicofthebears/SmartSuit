package SmartSuit.unipi.it.remoteControlApplication;

import SmartSuit.unipi.it.DatabaseAccess;

import java.sql.SQLException;
import java.util.HashMap;

public class PeriodicDataRetrieval implements Runnable{ //singleton class


    private static HashMap<String, Float> thresholds;
    private static final PeriodicDataRetrieval instance = new PeriodicDataRetrieval();

    private PeriodicDataRetrieval(){
        thresholds.put("radiation", 100.0f); //millisievert
        thresholds.put("gas", 35.0f); //PPM
        thresholds.put("electromagnetic", 40.0f); //V/m
    }

    public static PeriodicDataRetrieval getInstance(){
        return instance;
    }

    public void run() {
        try{
            HashMap<String, Float> values = DatabaseAccess.retrieveData();
            if (values.isEmpty()){
                System.out.println("Could not retrieve any data");
            }
            else{
                for(String key : values.keySet()){
                    if(values.get(key) > thresholds.get(key)){
                        CoAP_Client.actuatorCall(DatabaseAccess.retrieveActuator(key), key, "ON", true);
                    }
                }
            }
        }catch (SQLException e){
            System.out.println("Database error");
        }
    }

    public static void setRadiationThreshold(Float val){
        thresholds.put("radiation", val);
    }

    public static void setGasThreshold(Float val){
        thresholds.put("gas", val);
    }

    public static void setElectromagneticThreshold(Float val){
        thresholds.put("electromagnetic", val);
    }

}
