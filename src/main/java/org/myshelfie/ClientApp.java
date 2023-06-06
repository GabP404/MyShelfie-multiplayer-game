package org.myshelfie;

import org.myshelfie.network.client.Client;

import java.rmi.RemoteException;

public class ClientApp {

    public static void main( String[] args ) {

        Client client;
        try {
            client = new Client(true, false);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        client.run();
    }
}
