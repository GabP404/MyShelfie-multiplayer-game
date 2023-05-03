package org.myshelfie.network.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.myshelfie.controller.GameController;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.commandMessages.CommandMessageWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the Server class
 */
@ExtendWith(MockitoExtension.class)
public class ServerTest {
    @Mock
    private Client clientRMI;
    @Mock
    private Client clientSocket;
    @Mock
    private ServerSocket serverSocketMock;

    @Mock
    private Socket clientSocketMock;

    @Mock
    private BufferedReader inputMock;
    @Mock
    private GameController controllerMock;
    @InjectMocks
    private Server server = new Server(null);

    public ServerTest() throws RemoteException {
    }

    /**
     * Create two mock clients and register them to the server
     */
    @BeforeEach
    public void registerClientSetup() {
        //Mock the clients' getNickname() methods
        when(clientSocket.getNickname()).thenReturn("SocketTest");
        when(clientRMI.getNickname()).thenReturn("RMITest");
        assertDoesNotThrow(() -> server.register(clientRMI));
        assertDoesNotThrow(() -> server.register(clientSocket));
    }

    /**
     * Test the update method: it should only work on registered clients
     */
    @Test
    public void testUpdateClients(@Mock Client unregisteredClient, @Mock CommandMessageWrapper message) {
        assertDoesNotThrow(() -> server.update(clientRMI, message));
        assertDoesNotThrow(() -> server.update(clientRMI, message));
        assertThrows(IllegalArgumentException.class, () -> server.update(unregisteredClient, message));
    }

    @Test
    public void testStartRMIServer() {
        assertDoesNotThrow(() -> server.startRMIServer());
    }

    @Test
    public void testStartSocketServer() {
        Object lock = new Object();
        Thread serverStartThread = new Thread(() -> {
                server.startSocketServer(lock);
        });
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        // Redirect standard output to our print stream
        System.setOut(printStream);

        synchronized (lock) {
            serverStartThread.start();
            try {
                lock.wait(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Get the output from our output stream
        String output = outputStream.toString();

        assert output.contains("Server started with sockets.");
    }
}
