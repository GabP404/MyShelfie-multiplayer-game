package org.myshelfie;

import org.myshelfie.controller.Configuration;
import org.myshelfie.view.CLI.ViewCLI;
import org.myshelfie.view.GUI.ViewGUI;


public class ClientApp {

    /**
     *
     * Usage: java -jar client.jar [--cli | --gui] [--rmi | --socket] --server-address=<server-address>
     */
    public static void main( String[] args ) {
        boolean isGUI = true;
        Boolean isRMI = false;
        // Get the deafult server address from the configuration file
        String serverAddress = Configuration.getServerAddress();

        // For all the arguments, check if one of them is "--cli"
        // If so, set the isCLI variable to true
        for (String arg : args) {
            if (arg.equals("--cli")) {
                isGUI = false;
            }
            if (arg.equals("--gui")) {
                isGUI = true;
            }
            if (arg.equals("--rmi")) {
                isRMI = true;
            }
            if (arg.equals("--socket")) {
                isRMI = false;
            }
            // Get the server address
            if (arg.startsWith("--server-address=")) {
                serverAddress = arg.substring(17);
            }
        }

        String[] arguments = new String[2];
        arguments[0] = isRMI.toString();
        arguments[1] = serverAddress;
        if (isGUI) {
            ViewGUI.main(arguments); //Starts the JavaFX application
        } else {
            ViewCLI.main(arguments);
        }
    }
}
