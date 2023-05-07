package org.myshelfie.network;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.myshelfie.model.Game;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.server.Server;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Integration test for the client-server connection.
 * This class does not test the whole game logic, but only the connection between the client and the server.
 *
 * This class tests both the RMI and the Socket connection.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClientConnectToServerTest {
    private static Server server;
    private static Thread serverThread;

    @BeforeAll
    public static void setServerUp() {
        Object lock = new Object();
        serverThread = new Thread(() -> {
            try {
                server = new Server();
                server.startServer(lock);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();
        synchronized (lock) {
            try {
                lock.wait(1000);
            } catch (InterruptedException e) {
                System.out.println("Server thread interrupted");
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void testClientRMIConnectToServer() {
        String nickname = "RMITest";
        try {
            Client clientRMI = new Client(true, false);
            assertInstanceOf(Client.class, clientRMI);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.out.println("Nickname already in use");
        }
    }

    @AfterAll
    public static void stopServerThread() throws InterruptedException {
        System.out.println("Stopping server...");
        serverThread.interrupt();
        server.stopServer();
    }

    @Test
    public void testClientSocketConnectToServer() throws RemoteException {
        String nickname = "SocketTest";
        Client clientSocket = new Client( false, false);
        assertInstanceOf(Client.class, clientSocket);
    }
}
