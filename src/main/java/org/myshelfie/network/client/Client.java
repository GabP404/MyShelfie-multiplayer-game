package org.myshelfie.network.client;

import org.myshelfie.network.EventManager;
import org.myshelfie.network.messages.commandMessages.CommandMessageWrapper;
import org.myshelfie.network.messages.commandMessages.UserInputEvent;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.messages.gameMessages.EventWrapper;
import org.myshelfie.network.server.ServerRMIInterface;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements ClientRMIInterface {
    protected String nickname;
    protected static final String SERVER_ADDRESS = "localhost";
    protected static final int SERVER_PORT = 1234;

    protected static String RMI_SERVER_NAME = "MinecraftServer";
    ServerRMIInterface rmiServer;
    private Socket serverSocket;

    protected boolean isRMI;

    protected Socket clientSocket;
    public static EventManager eventManager = new EventManager();

    /**
     * Constructor used by the server to create a Client based on the nickname received via socket
     * @param nickname
     * @throws RemoteException
     */
    public Client(String nickname) throws RemoteException {
        super();
        this.nickname = nickname;
        this.isRMI = false;
    }

    public Client(String nickName, boolean isRMI) throws RemoteException {
        this.isRMI = isRMI;
        this.nickname = nickName;
        if (isRMI) {
            try {
                // Look up the server object in the RMI registry
                rmiServer = (ServerRMIInterface) Naming.lookup("//localhost/" + RMI_SERVER_NAME);
                rmiServer.register(this);
            } catch (Exception e) {
                System.err.println("Exception: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            try {
                // Create a new socket and connect to the server
                this.serverSocket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                PrintWriter output = new PrintWriter(serverSocket.getOutputStream(), true);
                output.println(nickName);

                // Create and start a new thread that constantly listens for messages from the server
                Thread serverListener = new SocketServerListener(serverSocket);
                serverListener.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // Subscribe a new UserInputListener that listen to changes in the view and forward events adding message to the server
        eventManager.subscribe(UserInputEvent.class, new UserInputListener(this));
    }

    class SocketServerListener extends Thread {
        private Socket serverSocket;

        // Socket client handler constructor
        public SocketServerListener(Socket socket) {
            this.serverSocket = socket;
        }

        // Thread function that will handle the client requests
        public void run() {
            try {
                // Create a new input stream to read from the server socket
                ObjectInputStream input = new ObjectInputStream(serverSocket.getInputStream());

                // Loop to handle every server message
                while (true) {
                    try {
                        // Read the message from the server
                        EventWrapper ew = (EventWrapper) input.readObject();
                        // If the request is null, the client has disconnected
                        if (ew == null) {
                            //TODO handle disconnection
                            System.out.println("Client disconnected.");
                            break;
                        }

                        Client.this.update(ew.getMessage(), ew.getType());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }

                // Close the server socket
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public Client(ClientRMIInterface rmiInterface) throws RemoteException {
        super();
        this.nickname = rmiInterface.getNickname();
    }

    public Client getClientInstance() {
        return this;
    }

    /**
     * Update of the client after the GameView has been modifies (or an Error has been received)
     * @param argument GameView in case of a model change, String in case of an error
     * @param ev Type of the information received
     */
    public void update(Object argument, GameEvent ev) {
        System.out.println("Received update from server: " + argument.toString() + " " + ev.toString());
    }

    public void updateServer(CommandMessageWrapper msg) {
        if (isRMI) {
            try {
                // Send command message to the server using RMI. Any possible error is handled by the server
                // so there is no need to wait for a server response
                rmiServer.update(this, msg);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        } else {
            // Send a serialized message to the server using the socket
            try {
                ObjectOutputStream output = new ObjectOutputStream(serverSocket.getOutputStream());
                output.writeObject(msg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isRMI() {
        return isRMI;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
}
