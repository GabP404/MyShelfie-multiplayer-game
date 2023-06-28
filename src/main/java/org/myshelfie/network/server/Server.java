package org.myshelfie.network.server;

import org.myshelfie.controller.Configuration;
import org.myshelfie.controller.GameController;
import org.myshelfie.controller.LobbyController;
import org.myshelfie.model.util.Pair;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.client.ClientRMIInterface;
import org.myshelfie.network.messages.commandMessages.*;
import org.myshelfie.network.messages.gameMessages.GameEvent;

import java.io.*;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;


public class Server extends UnicastRemoteObject implements ServerRMIInterface {
    private List<Client> clients;
    private LobbyController controller;
    public static ServerEventManager eventManager = new ServerEventManager();
    public static String SERVER_ADDRESS = Configuration.getServerAddress();
    private static Boolean RESUME_FROM_BACKUP = Boolean.FALSE;
    private String RMI_SERVER_NAME = Configuration.getServerRMIName();
    private ServerSocket serverSocket;
    private static final int HEARTBEAT_TIMEOUT = 10000; // TODO move to configuration

    private static Registry registry;

    // Logging utilities
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static Handler consoleHandler = null;
    private static Handler fileHandler  = null;


    /**
     * Main Server constructor. It sets the default logging level to INFO.
     * @throws RemoteException In case it is not possible to initialize the RMI server
     */
    public Server() throws RemoteException {
        super();
        this.clients = new ArrayList<>();
        this.controller = new LobbyController(this);

        // Prevent the logger from using parent handlers
        logger.setUseParentHandlers(false);

        // Initialize the logger
        consoleHandler = new ConsoleHandler();
        try {
            String logPath = Configuration.getServerLogFileName();
            fileHandler  = new FileHandler(logPath, true);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
        logger.setLevel(Configuration.getServerLogLevel());

        consoleHandler.setFormatter(new LoggingFormatterWithColor());
        fileHandler.setFormatter(new LoggingFormatter());

        logger.addHandler(consoleHandler);
        logger.addHandler(fileHandler);
    }

    /**
     * Server constructor with logging level parameter
     * @param loggingLevel the logging level to set
     * @throws RemoteException In case it is not possible to initialize the RMI server
     */
    public Server(Level loggingLevel) throws RemoteException {
        this();
        logger.setLevel(loggingLevel);
        consoleHandler.setLevel(loggingLevel);
        fileHandler.setLevel(loggingLevel);
        logger.info("Logging level set to " + loggingLevel);
    }

    public static void main( String[] args ) {
        // Usage example: java -jar server.jar [--server-address=<server-address>] [--backup] [--logging=<debug|info|error>]

        // Take the IP address from the CLI arguments to override the one in the configuration file
        // If the --backup flag is present, the server will try to resume from a backup file (serverBackup.ser)
        Level loggingLevel = null;
        for (String arg : args) {
            if (arg.startsWith("--server-address=")) {
                SERVER_ADDRESS = arg.substring(17);
            }
            if (arg.equals("--backup")) {
                RESUME_FROM_BACKUP = Boolean.TRUE;
            }
            if (arg.equals("--logging=debug")) {
                loggingLevel = Level.FINE;
            }
            if (arg.equals("--logging=info")) {
                loggingLevel = Level.INFO;
            }
            if (arg.equals("--logging=error")) {
                loggingLevel = Level.SEVERE;
            }
        }

        System.setProperty("java.rmi.server.hostname", SERVER_ADDRESS);
        Object lock = new Object();
        Server s = null;
        try {
            if (loggingLevel != null) {
                s = new Server(loggingLevel);
            } else {
                s = new Server();
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        s.startServer(lock);
    }


    public Client getClient(String nickname) {
        return this.clients.stream().filter(c -> c.getNickname().equals(nickname)).findFirst().orElse(null);
    }


    /**
     * Register a client to the server
     * @param client the client to register
     */
    public void register(Client client) throws IllegalArgumentException {
        //Throws an exception if there is already a client with the same nickname
        if (this.clients.stream().anyMatch(c -> c.getNickname().equals(client.getNickname()))) {
            throw new IllegalArgumentException("Nickname already taken");
        }
        this.clients.add(client);

        //Start a thread that will check the connection status of the client
        client.setLastHeartbeat(System.currentTimeMillis());
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(HEARTBEAT_TIMEOUT);
                    if (System.currentTimeMillis() - client.getLastHeartBeat() > HEARTBEAT_TIMEOUT) {
                        logger.info("Client " + client.getNickname() + " disconnected");
                        unregister(client);
                        controller.handleClientDisconnection(client.getNickname());
                        break;
                    }
                } catch (InterruptedException e) {
                    logger.info("Heartbeat thread for client " + client.getNickname() + " interrupted");
                }
            }
        });
        t.start();
        logger.info("Client " + client.getNickname() + " registered and heartbeat thread started.");
    }

    /**
     * Unregister a client from the server
     * @param client the client to unregister
     */
    public void unregister(Client client) {
        if (client == null) {
            return;
        }
        this.clients.remove(client);
        GameListener toUnsubscribe = (GameListener) eventManager.getListener(GameEvent.class, (l) -> {
            GameListener gl = (GameListener) l;
            return gl.getClient().getNickname().equals(client.getNickname());
        });
         eventManager.unsubscribe(GameEvent.class, toUnsubscribe);
        //The above is not needed I think (edit: instead it was lol)
    }

    /**
     * Update of the server after a client send a message. This method forwards the message produced by the View (which is
     * observed by the client) to the controller, specifying the client that generated the event.
     * @param clientRMIInterface  the client that generated the event
     * @param msg wrapped message received from the client
     */
    @Override
    public void update(ClientRMIInterface clientRMIInterface, CommandMessageWrapper msg) throws RemoteException {
        //Get the client from the nickname sent in the message
        Client client = this.getClient(msg.getMessage().getNickname());
        if (client == null) {
            // Ignore heartbeat messages, as they probably come from clients that have just been unregistered because
            // their game has ended
            if (msg.getType() == UserInputEvent.HEARTBEAT) {
                logger.warning("Received heartbeat from client " + msg.getMessage().getNickname() + " that is not registered anymore - ignoring");
                return;
            }
            throw new RemoteException("Client not registered!");
        }

        // unwrap the message
        UserInputEvent messageType = msg.getType();
        CommandMessage messageCommand = msg.getMessage();
        logger.fine("Server received event " + messageType + " from " + client.getNickname());

        // If the message is a heartbeat, update the last heartbeat time of the client and return (nothing to execute)
        if (messageType == UserInputEvent.HEARTBEAT) {
            client.setLastHeartbeat(System.currentTimeMillis());
            return;
        }
        // call the update on the controller
        this.controller.executeCommand(messageCommand, messageType);
    }

    /**
     * Update of the server after a client send a message. This method forwards the message produced by the View (which is
     * observed by the client) to the controller, specifying the client that generated the event.
     * @param clientRMIInterface  the client that generated the event
     * @param msg wrapped message received from the client
     */
    @Override
    public Object updatePreGame(ClientRMIInterface clientRMIInterface, CommandMessageWrapper msg) throws RemoteException {
        Client client = new Client(clientRMIInterface);
        logger.fine("Server received RMI event " + msg.getType());

        try {
            switch (msg.getType()) {
                case CREATE_GAME -> {
                    CreateGameMessage createGameMessage = (CreateGameMessage) msg.getMessage();
                    return this.createGame(createGameMessage);
                }
                case JOIN_GAME -> {
                    JoinGameMessage joinGameMessage = (JoinGameMessage) msg.getMessage();
                    return this.joinGame(joinGameMessage);
                }
                case NICKNAME -> {
                    NicknameMessage nicknameMessage = (NicknameMessage) msg.getMessage();
                    // set the nickname of the client, but if the register fails, the nickname will be set again
                    client.setNickname(nicknameMessage.getNickname());
                    try {
                        this.register(client);
                        logger.info("Client " + client.getNickname() + " registered");
                        // Put the client back in the game, if necessary
                        boolean reconnecting = this.controller.handleClientReconnection(client.getNickname());
                        if (reconnecting) {
                            return new Pair<ConnectingStatuses, Object>(ConnectingStatuses.RECONNECTING, this.controller.getGames());
                        } else {
                            return new Pair<ConnectingStatuses, Object>(ConnectingStatuses.CONFIRMED, this.controller.getGames());
                        }
                    } catch (IllegalArgumentException e) {
                        logger.warning("Nickname already taken");
                        return new Pair<ConnectingStatuses, Object>(ConnectingStatuses.ERROR, new ArrayList<>());
                    }
                }
                case REFRESH_AVAILABLE_GAMES -> {
                    // send the current list of games
                    return this.controller.getGames();
                }
                default -> throw new IllegalArgumentException("Wrong message type");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(e.getMessage());
        }
    }

    // Method to start the server
    public void startServer(Object lock) {
        try {
            // Start the RMI server
            startRMIServer();
            // Start the socket server
            startSocketServer(lock);
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // Method to start the RMI server
    public void startRMIServer() throws RemoteException, MalformedURLException {
        registry = null;
        try {
            // Create the RMI registry
            registry = LocateRegistry.createRegistry(1099);
            registry.bind("//" + SERVER_ADDRESS + "/" + RMI_SERVER_NAME, this);
        } catch (AlreadyBoundException e) {
            // If the server is already bound, we unbind it and bind it again
            registry.rebind("//" + SERVER_ADDRESS + "/" + RMI_SERVER_NAME, this);
        }

        // Bind the server object to the registry
        logger.info("Server started with RMI.");
    }

    /**
     * Start the socket server
     * @param lock the lock to notify when the server is started
     */
    public void startSocketServer(Object lock) {
        try {
            synchronized (lock) {
                serverSocket = new ServerSocket(1234);
                // Create a new server socket
                logger.info("Server started with sockets.");
                lock.notifyAll();
            }

            // Loop to handle multiple connections
            while (true) {
                Socket clientSocket = null;
                try {
                    // Accept a new client connection
                    clientSocket = serverSocket.accept();
                    logger.fine("Accepted new socket connection.");
                } catch (SocketException e) {
                    logger.fine("Socket closed.");
                    return;
                }

                //Create and register a new client
                Client client = new Client();
                client.setClientSocket(clientSocket);
                // Create and start a new client handler thread
                Thread clientHandler = new SocketClientHandler(clientSocket, client);
                clientHandler.start();

            }
        } catch (IOException e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Stop the socket server
     */
    public void stopSocketServer() {
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop the RMI server
     */
    public void stopRMIServer() {
        try {
            Naming.unbind(RMI_SERVER_NAME);
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop the server (both RMI and socket)
     */
    public void stopServer() {
        stopSocketServer();
        stopRMIServer();
    }

    /**
     * Method to send a message to a client
     * @param clientSocket
     */
    public void sendTo(Socket clientSocket, Serializable message) {
        ObjectOutputStream output;
        try {
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            output.writeObject(message);
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Inner class to handle client connections
    class SocketClientHandler extends Thread {
        private Socket clientSocket;
        private Client client;

        // Socket client handler constructor
        public SocketClientHandler(Socket socket, Client client) {
            this.clientSocket = socket;
            this.client = client;
        }

        // Thread function that will handle the client requests
        public void run() {
            try {
                // Create a new input stream to read serialized objects from the client socket
                ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());

                //Get client nickname
                boolean inputValid = false;
                boolean reconnecting = false;
                Pair<ConnectingStatuses, List<GameController.GameDefinition>> response;
                do {
                    try {
                        CommandMessageWrapper messageWrapper = (CommandMessageWrapper) input.readObject();
                        client.setNickname(messageWrapper.getMessage().getNickname());
                        Server.this.register(client);
                        // Put the client back in the game, if it is reconnecting
                        reconnecting = Server.this.controller.handleClientReconnection(client.getNickname());
                        inputValid = true;
                    } catch (IllegalArgumentException e) {
                        // The nickname is probably already taken!
                        logger.fine("Generated IllegalArgumentException before sending response.");
                        response = new Pair<>(ConnectingStatuses.ERROR, new ArrayList<>());
                        sendTo(clientSocket, response);
                    } catch (EOFException e) {
                        // If this exception is caught, the client has disconnected
                        logger.fine("Socket stream reached EOF - client disconnected.");
                        return; // Terminate the thread since the client has disconnected
                    } catch (SocketException e) {
                        // If this exception is caught, the client has disconnected
                        logger.fine("Socket exception caught - client disconnected.");
                        return; // Terminate the thread since the client has disconnected
                    }
                } while (!inputValid);

                if (reconnecting) {
                    // The client is reconnecting to a game.
                    // Skip sending the list of games and wait directly for the game messages.
                    logger.fine("Sending list of games to RECONNECTING client " + client.getNickname());
                    response = new Pair<>(ConnectingStatuses.RECONNECTING, Server.this.getGames());
                    sendTo(clientSocket, response);
                } else {
                    // The client is not reconnecting to any game.
                    // Send confirm and list of games
                    logger.fine("Sending list of games to client " + client.getNickname());
                    response = new Pair<>(ConnectingStatuses.CONFIRMED, Server.this.getGames());
                    sendTo(clientSocket, response);

                    // Get CREATE or JOIN or REFRESH_AVAILABLE_GAMES game message
                    inputValid = false;
                    do {
                        try {
                            CommandMessageWrapper message = (CommandMessageWrapper) input.readObject();
                            logger.fine("Received message of type '" + message.getType() + "' from client " + client.getNickname());
                            if (message.getType() == UserInputEvent.CREATE_GAME) {
                                inputValid = Server.this.createGame((CreateGameMessage) message.getMessage());
                            } else if (message.getType() == UserInputEvent.JOIN_GAME) {
                                inputValid = Server.this.joinGame((JoinGameMessage) message.getMessage());
                                if (!inputValid) {
                                    // Alert the client that the game is not joinable for some reason
                                    sendTo(clientSocket, false);
                                }
                            } else if (message.getType() == UserInputEvent.REFRESH_AVAILABLE_GAMES) {
                                // send the list of available games WITHOUT changing the inputValid flag
                                sendTo(clientSocket, (Serializable) Server.this.getGames());
                            } else if (message.getType() == UserInputEvent.HEARTBEAT) {
                                client.setLastHeartbeat(System.currentTimeMillis());
                            }  else {
                                throw new IllegalArgumentException("Invalid message type");
                            }
                        } catch (IllegalArgumentException e) {
                            sendTo(clientSocket, e.getMessage());
                        } catch (EOFException e) {
                            // If this exception is caught, the client has disconnected
                            logger.fine("Socket stream reached EOF - probably disconnected. Setting last heartbeat to 0.");
                            client.setLastHeartbeat(0);
                            return; // Terminate the thread since the client has disconnected
                        } catch (SocketException e) {
                            // If this exception is caught, the client has disconnected
                            logger.fine("Socket exception caught - probably disconnected. Setting last heartbeat to 0.");
                            client.setLastHeartbeat(0);
                            return; // Terminate the thread since the client has disconnected
                        }
                    } while (!inputValid);

                    // Send flag to signal the successful creation / join of the game
                    sendTo(clientSocket, inputValid);
                }

                // Wait for UserInputEvents related to the game
                while (true) {
                    try {
                        // Read a request from the client, sent as a serialized CommandMessageWrapper
                        CommandMessageWrapper request = (CommandMessageWrapper) input.readObject();

                        // Handle the request
                        Server.this.update(this.client, request);
                    } catch (EOFException e) {
                        // If this exception is caught, the client has disconnected
                        logger.fine("Socket stream reached EOF - probably disconnected. Setting last heartbeat to 0.");
                        client.setLastHeartbeat(0);
                        break;
                    } catch (SocketException e) {
                        // If this exception is caught, the client has disconnected
                        logger.fine("Socket exception caught - probably disconnected. Setting last heartbeat to 0.");
                        client.setLastHeartbeat(0);
                        break;
                    } catch (ClassNotFoundException | InvalidClassException | StreamCorruptedException e) {
                        logger.severe(
                                "Invalid message received from client " + client.getNickname() +
                                "\nThe error is probably due to the deserialization process." +
                                "\nError message: " + e.getMessage() +
                                "Continuing to play, but the status may be inconsistent."
                        );
                    }
                }

                // Close the client socket and unregister the client
                clientSocket.close();
                Server.this.clients.remove(client);
            } catch (IOException e) {
                logger.severe("Exception: " + e.getMessage());
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                logger.severe("ClassNotFound exception: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Handle the heartbeat message sent by a client
     * @param client The client (RMIInterface) that sent the heartbeat
     * @throws RemoteException
     */
    @Override
    public void heartbeat(ClientRMIInterface client, HeartBeatMessage msg) throws RemoteException {
        Client c = getClient(msg.getNickname());
        if (c == null) {
            //The client is not registered!
            throw new RemoteException("Client not registered!");
        }

        logger.fine("Received heartbeat from client " + c.getNickname());
        //Update the last heartbeat timestamp
        c.setLastHeartbeat(System.currentTimeMillis());
    }

    public List<GameController.GameDefinition> getGames() throws RemoteException {
        return this.controller.getGames();
    }

    public boolean createGame(CreateGameMessage message) throws RemoteException {
        try {
            this.controller.createGame(message);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean joinGame(JoinGameMessage message) throws RemoteException {
        try {
            this.controller.joinGame(message);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean shouldResumeFromBackup()
    {
        return RESUME_FROM_BACKUP;
    }

    /**
     * Log a message
     * @param logLevel The log level (FINE, INFO, WARNING, SEVERE, ...)
     * @param message The message to log
     */
    public void log(Level logLevel, String message) {
        logger.log(logLevel, message);
    }
}

