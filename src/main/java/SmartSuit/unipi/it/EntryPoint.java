package SmartSuit.unipi.it;

import SmartSuit.unipi.it.cloudApplication.CoAP_Registration;
import SmartSuit.unipi.it.cloudApplication.MQTT_Collector;
import SmartSuit.unipi.it.remoteControlApplication.CommandLineInterface;
import SmartSuit.unipi.it.remoteControlApplication.PeriodicDataRetrieval;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EntryPoint {
    public static void main(String[] args) {

        ScheduledExecutorService executorService = null;
        try {
            executorService = Executors.newScheduledThreadPool(4);
            executorService.schedule(CommandLineInterface.getInstance(), 120, TimeUnit.SECONDS);
            executorService.schedule(CoAP_Registration.getInstance(), 0, TimeUnit.SECONDS);
            executorService.schedule(MQTT_Collector.getInstance(), 0, TimeUnit.SECONDS);
            executorService.scheduleAtFixedRate(PeriodicDataRetrieval.getInstance(), 120, 7, TimeUnit.SECONDS);
        } catch (Exception e) {
            executorService.shutdown();
        }


    }
}

