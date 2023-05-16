package org.myshelfie;

import org.myshelfie.model.*;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.server.Server;

import java.rmi.RemoteException;
import java.util.*;

import static org.myshelfie.model.ModelState.WAITING_SELECTION_TILE;

public class TestCLI implements Runnable {

    private Game game;
    private static Server server;

    private static Thread serverThread;
    private Client client;

    @Override
    public void run() {

        try {
            client = new Client(true, false);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        client.run();
    }
}
