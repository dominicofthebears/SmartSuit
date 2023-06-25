package SmartSuit.unipi.it.remoteControlApplication;

import SmartSuit.unipi.it.DatabaseAccess;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class CommandLineInterface implements Runnable{

    private static final CommandLineInterface instance = new CommandLineInterface();

    private CommandLineInterface(){
    }

    public static CommandLineInterface getInstance(){
        return instance;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to SmartSuit system management\n" +
                "The available commands are the following, you can execute them by inserting the corresponding number\n" +
                "1 - Help list\n" +
                "2 - Select a new value for the radiation threshold\n" +
                "3 - Select a new value for the gas threshold\n" +
                "4 - Select a new value for the electromagnetic threshold\n" +
                "5 - Change the status of the radiation shield\n" +
                "6 - Change the status of the oxygen flow system\n" +
                "7 - Change the status of the electromagnetic shield\n" +
                "8 - Check the actuators status");
        while (true){

            System.out.println("Insert a command: ");
            int input = Integer.parseInt(scanner.nextLine().trim());

            switch (input){

                case 1:
                    helpList();
                    break;

                case 2:
                    changeThreshold("radiation", scanner);
                    break;

                case 3:
                    changeThreshold("gas", scanner);
                    break;

                case 4:
                    changeThreshold("electromagnetic", scanner);
                    break;

                case 5:
                    changeStatus("radiation", scanner);
                    break;

                case 6:
                    changeStatus("gas", scanner);
                    break;

                case 7:
                    changeStatus("electromagnetic", scanner);
                    break;

                case 8:
                    try {
                        printActuatorsStatus();
                    } catch (SQLException e) {
                        System.out.println("impossible to retrieve actuator status");
                    }
                    break;
            }
        }

    }

    private static void printActuatorsStatus() throws SQLException {
        String[] actuators = new String[]{"gas", "electromagnetic", "radiation"};
        for(String key: actuators){
            System.out.println("The " + key + " actuator status is: " + DatabaseAccess.retrieveActuator(key).get("status"));
        }
    }
    private static void changeStatus(String actuator, Scanner s) {
        try{
            System.out.println("Would you like to turn the actuator ON or OFF?: ");
            String input = (s.nextLine().trim());
            System.out.println(input);
            HashMap<String, String> act = DatabaseAccess.retrieveActuator(actuator);
            if(Objects.equals(act.get("status"), input)){
                System.out.println("Actuator already in this status");
            }
            else {
                CoAP_Client.actuatorCall(act.get("ip"), actuator, input, 0);
            }
        } catch (SQLException e){
            System.out.println("Could not retrieve the actuators");
        }

    }

    private static void changeThreshold(String sensor, Scanner s) {
        System.out.println("Insert the new value: ");
        int input = Integer.parseInt(s.nextLine().trim());
        switch (sensor){
            case "radiation":
                PeriodicDataRetrieval.setRadiationThreshold(input);
                break;
            case "gas":
                PeriodicDataRetrieval.setGasThreshold(input);
                break;
            case "electromagnetic":
                PeriodicDataRetrieval.setElectromagneticThreshold(input);
                break;
        }
        System.out.println("Value changed");
    }

    private static void helpList(){
        System.out.println(
                "The available commands are the following, you can execute them by inserting the corresponding number\n" +
                "1 - Help list\n" +
                "2 - Select a new value for the radiation threshold\n" +
                "3 - Select a new value for the gas threshold\n" +
                "4 - Select a new value for the gas threshold\n" +
                "5 - Change the status of the radiation shield\n" +
                "6 - Change the status of the oxygen flow system\n" +
                "7 - Change the status of the electromagnetic shield\n"+
                "8 - Check the actuators status");
    }

}
