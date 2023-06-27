package org.myshelfie.network.client;

import org.myshelfie.controller.Configuration;
import org.myshelfie.network.EventManager;
import org.myshelfie.network.messages.commandMessages.CommandMessageWrapper;
import org.myshelfie.network.messages.commandMessages.HeartBeatMessage;
import org.myshelfie.network.messages.gameMessages.EventWrapper;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.messages.gameMessages.GameView;
import org.myshelfie.network.server.ServerRMIInterface;
import org.myshelfie.view.View;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * This class represents a client that can connect to the server via RMI or Socket.
 * The client is a network object used by the CLI and GUI.
 */
public class Client extends UnicastRemoteObject implements ClientRMIInterface, Runnable{

    // Add a heartbeat interval constant
    private static final int HEARTBEAT_INTERVAL = Configuration.getHeartbeatInterval();
    private long lastHeartbeat = System.currentTimeMillis();

    private Thread heartbeatThread; // Thread that sends heartbeats to the server

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
     * Constructor used by the Server class to create a Client based on the nickname received via socket
     * @throws RemoteException if the remote object cannot be exported
     */
    public Client() throws RemoteException {
        super();
        this.isRMI = false;
    }

    /**
     * Constructor for the client.
     * @param isGUI true if the client is a GUI, false if it is a CLI
     * @param isRMI true if the client uses RMI, false if it uses Socket
     * @param serverAddress the address of the server. Can be a hostname or an IP address
     * @throws RemoteException if the remote object cannot be exported
     */
    public Client(boolean isGUI, boolean isRMI, String serverAddress) throws RemoteException {
        SERVER_ADDRESS = serverAddress;

        // Save the choice of the user
        this.isRMI = isRMI;

        // Subscribe a new UserInputListener that listen to changes in the view and forward events adding message to the server
        eventManager.subscribe(UserInputEvent.class, new UserInputListener(this));
    }

    /**
     * Establish the connection to the server, be it RMI or Socket.
     */
    public void connect() {
        try {
            if (isRMI) {
                // Resolve the hostname, if necessary
                InetAddress address = InetAddress.getByName(SERVER_ADDRESS);
                String hostAddress = address.getHostAddress();

                // Look up the server object in the RMI registry
                Registry registry = LocateRegistry.getRegistry(hostAddress, 1099);
                rmiServer = (ServerRMIInterface) registry.lookup("//" + hostAddress + "/" + RMI_SERVER_NAME);
            } else {
                // Create a new socket and connect to the server
                this.serverSocket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                output = new ObjectOutputStream(serverSocket.getOutputStream());

                // Create and start a new thread that constantly listens for messages from the server
                serverListener = new SocketServerListener(serverSocket);
            }
        } catch (NoRouteToHostException e) {
            System.out.println("No route to server! Please specify another valid server address.");
            System.exit(1);
        } catch (AccessException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            System.out.println("Host unknown! Please specify another server address.");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Server not bound! The provided RMI name for the server is not valid, please change it.");
            System.exit(1);
        } catch (RemoteException e) {
            System.out.println("The reference to the RMI registry could not be created! Please check your connection.");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Error opening the socket: " + e.getMessage());
            System.exit(1);
        }
    }


    /**
     * End the login phase in the pre-game stage.
     * This calls the {@link View#endLoginPhase} method, to change the scene accordingly.
     */
    public void endLoginPhase() {
        view.endLoginPhase();
    }

    /**
     * End the lobby phase in the pre-game stage.
     * This calls the {@link View#endLobbyPhase} method, to change the scene accordingly.
     */
    public void endLobbyPhase() {
        view.endLobbyPhase();
        startServerListener();
    }

    /**
     * Start to listen for messages from the server via socket.
     */
    public void startServerListener() {
        if (!isRMI) {
            try {
                serverListener.start();
            } catch (java.lang.IllegalThreadStateException e) {
                // Should never be thrown, but if thrown, then thread was already started!
            }
        }
    }

    /**
     * Getter for the name of the game the client is playing in.
     * @return the name of the game
     */
    public String getGameName() {
        return view.getGameName();
    }

    /**
     * Setter for the last heartbeat of the client
     * @param l the last heartbeat, as a timestamp
     */
    public void setLastHeartbeat(long l) {
        this.lastHeartbeat = l;
    }

    /**
     * Getter for the last heartbeat of the client
     * @return the last heartbeat, as a timestamp
     */
    public long getLastHeartBeat() {
        return this.lastHeartbeat;
    }

    /**
     * Assign a view to the client.
     * @param view the view to assign (either {@link org.myshelfie.view.GUI.ViewGUI} or {@link org.myshelfie.view.CLI.ViewCLI}
     */
    public void initializeView(View view) {
        this.view = view;
    }

    /**
     * Inner class used to listen for messages from the server via socket.
     */
    class SocketServerListener extends Thread {
        private Socket serverSocket;

        /**
         * Socket client handler constructor
         */
        public SocketServerListener(Socket socket) {
            this.serverSocket = socket;
        }

        /**
         * Run method of the thread.
         * Handles all the messages that come from the socket server.
         */
        @Override
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

    /**
     * Constructor for the client, used in the RMI version, to get a Client object starting
     * from the published RMI interface.
     * @param rmiInterface the RMI interface of the client
     * @throws RemoteException if the RMI connection fails
     */
    public Client(ClientRMIInterface rmiInterface) throws RemoteException {
        super();
        this.RMIInterface = rmiInterface;
        this.isRMI = true;
        this.nickname = rmiInterface.getNickname();
    }

    /**
     * Getter for the view of the client
     * @return the View of the client
     */
    public View getView() {
        return this.view;
    }

    /**
     * Update of the client after the {@link GameView} has been modified (or an Error has been received)
     * @param argument GameView in case of a model change, String in case of an error
     * @param ev Type of the information received
     */
    @Override
    public void update(GameView argument, GameEvent ev) throws RemoteException {
        view.update(argument, ev);
    }

    /**
     * Remote method called by the server to update an RMI client. Will call the {@link #update} method
     * @param argument GameView in case of a model change, String in case of an error
     * @param ev Type of the information received
     * @throws RemoteException if the RMI connection fails
     */
    public void updateRMI(GameView argument, GameEvent ev) throws RemoteException {
        this.RMIInterface.update(argument, ev);
    }

    /**
     * Start a heartbeat thread, that will periodically send a heartbeat message to the server.
     */
    public void startHeartBeatThread() {
        if (isRMI) {
            heartbeatThread = new Thread(this::sendHeartbeatRMI);
        } else {
            heartbeatThread = new Thread(this::sendHeartbeatSocket);
        }
        heartbeatThread.start();
        System.out.println("Heartbeat thread started!");
    }

    /**
     * Stop the thread that periodically send the heartbeat message to the server.
     */
    public void stopHeartbeatThread() {
        heartbeatThread.interrupt();
    }

    @Override
    public void run()
    {
        view.run();
    }

    /**
     * Send an update to the server after a user performs an action, using RMI or Socket depending on the type of client
     * @param msg Message containing information about the command to send to the server
     */
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
     * This calls the remote method heartbeat() of the server and should thus be used only by an "RMI" client.
     * This method is supposed to be run inside a dedicated thread.
     */
    private void sendHeartbeatRMI() {
        while (true) {
            try {
                // Send a heartbeat message to the server using RMI
                HeartBeatMessage msg = new HeartBeatMessage(this.nickname);
                rmiServer.heartbeat(this, msg);
                Thread.sleep(HEARTBEAT_INTERVAL);
            } catch (InterruptedException | RemoteException e) {
                // Thread was stopped, so the game is probably over. Terminate the thread
                break;
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
                // Thread was stopped, so the game is probably over. Terminate the thread
                break;
            }
        }
    }


    /**
     * Getter for the nickname of the client
     * @return the nickname of the client
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Setter for the nickname of the client
     * @param nickname the new nickname of the client
     */
    public void setNickname(String nickname) {
    	this.nickname = nickname;
    }

    /**
     *
     * @return true if the client is an RMI client, false if it is a socket client
     */
    public boolean isRMI() {
        return isRMI;
    }

    /**
     * Get the client socket, to which the server can send messages
     * @return the Socket of the client
     */
    public Socket getClientSocket() {
        return clientSocket;
    }

    /**
     * Set the client socket, to save the reference to which send messages
     * @param clientSocket the new Socket of the client
     */
    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
}
