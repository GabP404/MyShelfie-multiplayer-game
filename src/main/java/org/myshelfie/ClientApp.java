package org.myshelfie;

import org.myshelfie.network.client.Client;

import java.rmi.RemoteException;
import java.util.Scanner;

import static org.myshelfie.view.ViewCLI.*;


public class ClientApp {

    public static void main( String[] args ) {
        Scanner userInput = new Scanner(System.in);
        boolean isRMI = false;
        String choice;
        clear();
        printTitle();
        print("Would you like to use Socket or RMI? (s/r)", 0, 20, false);
        do {
            setCursor(0, 22);
            choice = userInput.nextLine();
            if (choice.equalsIgnoreCase("s"))
                isRMI = false;
            else if (choice.equalsIgnoreCase("r"))
                isRMI = true;
            else
            {
                clear();
                print("Try again ", 0, 25, false);
                printTitle();
                print("Would you like to use Socket or RMI? (s/r)", 0, 20, false);
            }
        } while(!choice.equalsIgnoreCase("s") && !choice.equalsIgnoreCase("r"));

        Client client;
        try {
            client = new Client(isRMI, false);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        client.run();
    }
}
