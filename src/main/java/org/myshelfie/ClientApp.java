package org.myshelfie;

import org.myshelfie.controller.Configuration;
import org.myshelfie.network.client.Client;

import java.rmi.RemoteException;


public class ClientApp {

    // Usage: java -jar client.jar [--cli] --server-address=<server-address>
    public static void main( String[] args ) {
        boolean isGUI = true;
        String serverAddress = Configuration.getServerAddress();

        // For all the arguments, check if one of them is "--cli"
        // If so, set the isCLI variable to true
        for (String arg : args) {
            if (arg.equals("--cli")) {
                isGUI = false;
            }
            // Get the server address
            if (arg.startsWith("--server-address=")) {
                serverAddress = arg.substring(18);
            }
        }

        Client client;
        try {
            client = new Client(isGUI, serverAddress);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
