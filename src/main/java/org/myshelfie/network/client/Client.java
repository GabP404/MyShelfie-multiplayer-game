package org.myshelfie.network.client;

import org.myshelfie.controller.Configuration;
import org.myshelfie.network.EventManager;
import org.myshelfie.network.messages.commandMessages.CommandMessageWrapper;
import org.myshelfie.network.messages.commandMessages.HeartBeatMessage;
import org.myshelfie.network.messages.commandMessages.UserInputEvent;
import org.myshelfie.network.messages.gameMessages.EventWrapper;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.messages.gameMessages.GameView;
import org.myshelfie.network.server.ServerRMIInterface;
import org.myshelfie.view.View;
import org.myshelfie.view.CLI.ViewCLI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import static org.myshelfie.view.PrinterCLI.*;
import static org.myshelfie.view.PrinterCLI.print;

public class Client extends UnicastRemoteObject implements ClientRMIInterface, Runnable{

    // Add a heartbeat interval constant
    private static final int HEARTBEAT_INTERVAL = 5000; // 5 seconds
    private long lastHeartbeat = System.currentTimeMillis();

    protected String nickname;
    protected static String SERVER_ADDRESS = Configuration.getServerAddress();
    protected static final int SERVER_PORT = Configuration.getServerSocketPort();

    protected static String RMI_SERVER_NAME = Configuration.getServerRMIName();
    ServerRMIInterface rmiServer;
    private Socket serverSocket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    protected boolean isRMI;
    private ClientRMIInterface RMIInterface;
    private Thread serverListener;

    protected Socket clientSocket;
    public EventManager eventManager = new EventManager();
    private View view;
    private String gameName;

    /**
     * Constructor used by the server to create a Client based on the nickname received via socket
     * @throws RemoteException
     */
    public Client() throws RemoteException {
        super();
        this.isRMI = false;
    }

    public Client(boolean isGUI, String serverAddress) throws RemoteException {
        SERVER_ADDRESS = serverAddress;

        // Subscribe a new UserInputListener that listen to changes in the view and forward events adding message to the server
        eventManager.subscribe(UserInputEvent.class, new UserInputListener(this));

        if (isGUI) {
            // TODO: implement GUI
        } else {
            Scanner userInput = new Scanner(System.in);
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

            this.view = new ViewCLI(this);
            print("Connecting to server...", 0, 25, false);
            // Connect to the server
            this.connect();
            this.run();
        }

    }

    public void connect() {
        // connect
        if (isRMI) {
            try {
                // Look up the server object in the RMI registry
                Registry registry = LocateRegistry.getRegistry(SERVER_ADDRESS, 1099);
                rmiServer = (ServerRMIInterface) registry.lookup("//" + SERVER_ADDRESS + "/" + RMI_SERVER_NAME);
            } catch (Exception e) {
                System.err.println("Exception: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            try {
                // Create a new socket and connect to the server
                this.serverSocket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                output = new ObjectOutputStream(serverSocket.getOutputStream());

                // Create and start a new thread that constantly listens for messages from the server
                serverListener = new SocketServerListener(serverSocket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void endNicknameThread() {
        view.endLoginPhase();
    }

    public void endChoiceThread() {
        view.endLobbyPhase();
        if (!isRMI) {
            try {
                serverListener.start();
            } catch (java.lang.IllegalThreadStateException e) {
                // Should never be thrown, but if thrown, then thread was already started!
            }
        }
    }

    public void startServerListener() {
        if (!isRMI) {
            try {
                serverListener.start();
            } catch (java.lang.IllegalThreadStateException e) {
                // Should never be thrown, but if thrown, then thread was already started!
            }
        }
    }

    public String getGameName() {
        return view.getGameName();
    }

    public void setLastHeartbeat(long l) {
        this.lastHeartbeat = l;
    }

    public long getLastHeartBeat() {
        return this.lastHeartbeat;
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
                // Loop to handle every server message
                while (true) {
                    try {
                        // Read the message from the server
                        input = new ObjectInputStream(serverSocket.getInputStream());
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
        this.RMIInterface = rmiInterface;
        this.isRMI = true;
        this.nickname = rmiInterface.getNickname();
    }

    public View getView() {
        return this.view;
    }

    public Client getClientInstance() {
        return this;
    }

    /**
     * Update of the client after the GameView has been modifies (or an Error has been received)
     * @param argument GameView in case of a model change, String in case of an error
     * @param ev Type of the information received
     */
    @Override
    public void update(GameView argument, GameEvent ev) throws RemoteException {
        view.update(argument, ev);
    }

    public void updateRMI(GameView argument, GameEvent ev) throws RemoteException {
        this.RMIInterface.update(argument, ev);
    }

    public void startHeartBeatThread() {
        Thread heartbeatThread;
        if (isRMI) {
            heartbeatThread = new Thread(this::sendHeartbeatRMI);
        } else {
            heartbeatThread = new Thread(this::sendHeartbeatSocket);
        }
        heartbeatThread.start();
        System.out.println("Heartbeat thread started!");
    }

    @Override
    public void run()
    {
        view.run();
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
                output.writeObject(msg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Update the server during pre-game session by handling separetely RMI and Socket
     * @param msg Message containing information about the event (NICKNAME, CREATE_GAME or JOIN_GAME)
     * @return Server response, whose type depends on the type of event
     */
    public Object updateServerPreGame(CommandMessageWrapper msg) {
        if (isRMI) {
            try {
                return rmiServer.updatePreGame(this, msg);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        } else {
            // Send a serialized message to the server using the socket
            try {
                output.writeObject(msg);

                input = new ObjectInputStream(serverSocket.getInputStream());
                return input.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Exception: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Send a heartbeat message to the server every HEARTBEAT_INTERVAL milliseconds.
     * This calls the remote method heartbeat() of the server.
     * This method is supposed to be run inside a dedicated thread.
     */
    private void sendHeartbeatRMI() {
        while (true) {
            try {
                // Send a heartbeat message to the server using RMI
                HeartBeatMessage msg = new HeartBeatMessage(this.nickname);
                rmiServer.heartbeat(this, msg);
                Thread.sleep(HEARTBEAT_INTERVAL);
            } catch (RemoteException | InterruptedException e) {
                // Handle exceptions as needed
            }
        }
    }

    /**
     * Send a heartbeat message to the server every HEARTBEAT_INTERVAL milliseconds.
     * This method should be used only by a "socket" client, and is supposed to be run inside a dedicated thread.
     */
    private void sendHeartbeatSocket() {
        while (true) {
            try {
                // Send a heartbeat message to the server
                CommandMessageWrapper heartbeatMsg = new CommandMessageWrapper(
                        new HeartBeatMessage(this.nickname),
                        UserInputEvent.HEARTBEAT
                );
                output.writeObject(heartbeatMsg);
                Thread.sleep(HEARTBEAT_INTERVAL);
            } catch (IOException | InterruptedException e) {
                // Handle exceptions as needed
            }
        }
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
    	this.nickname = nickname;
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
