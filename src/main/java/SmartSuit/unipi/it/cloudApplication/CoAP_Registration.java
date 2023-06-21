package SmartSuit.unipi.it.cloudApplication;

import SmartSuit.unipi.it.cloudApplication.resources.RegistrationResource;
import org.eclipse.californium.core.CoapServer;

public class CoAP_Registration extends CoapServer {

    public static void main(String args[]) {

        CoAP_Registration server = new CoAP_Registration();
        server.add(new RegistrationResource("registration"));
        server.start();
        System.out.println("Starting the registration server");
    }

}
