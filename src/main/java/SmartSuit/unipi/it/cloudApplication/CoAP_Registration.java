package SmartSuit.unipi.it.cloudApplication;

import SmartSuit.unipi.it.cloudApplication.resources.RegistrationResource;
import org.eclipse.californium.core.CoapServer;

public class CoAP_Registration extends CoapServer implements Runnable {

    private static final CoAP_Registration instance = new CoAP_Registration();

    private CoAP_Registration(){
    }

    public static CoAP_Registration getInstance(){
        return instance;
    }

    public void run() {
        CoAP_Registration server = new CoAP_Registration();
        server.add(new RegistrationResource("registration"));
        server.start();
        System.out.println("Starting the registration server");
    }

}
