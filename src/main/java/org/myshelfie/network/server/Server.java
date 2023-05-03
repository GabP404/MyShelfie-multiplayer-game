package org.myshelfie.network.server;

import org.myshelfie.controller.GameController;
import org.myshelfie.controller.InvalidCommand;
import org.myshelfie.controller.WrongTurnException;
import org.myshelfie.model.Game;
import org.myshelfie.model.WrongArgumentException;
import org.myshelfie.network.EventManager;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.client.ClientRMIInterface;
import org.myshelfie.network.messages.commandMessages.CommandMessageWrapper;
import org.myshelfie.network.messages.commandMessages.UserInputEvent;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.messages.gameMessages.EventWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;


public class Server extends UnicastRemoteObject implements ServerRMIInterface {
    private List<Client> clients;
    private GameController controller;
    public static EventManager eventManager = new EventManager();
    private Game game;
    private String RMI_SERVER_NAME = "MinecraftServer";
    private ServerSocket serverSocket;

    /**
     * Overloaded constructor used for testing since it allows to initialize the Game object outside
     * @param game Already initialized model
     */
    public Server(Game game) throws RemoteException {
        super();
        this.game = game;
        this.clients = new ArrayList<>();
        this.controller = new GameController();
    }

    /**
     * Getter for the model that the server is using. This method is used in order to allow GameListener to send
     * the updated modelView everytime a change occurs in the model.
     * NOTE: this method will need to be parametric when we'll handle multiple games.
     * @return The model used by the server
     */
    Game getGame() {
        return game;
    }

    /**
     * Register a client to the server
     * @param client the client to register
     */
    public void register(Client client) {
        //Throws an exception if there is already a client with the same nickname
        if (this.clients.stream().anyMatch(c -> c.getNickname().equals(client.getNickname()))) {
            throw new IllegalArgumentException("Nickname already taken");
        }
        this.clients.add(client);
        // Subscribe a new GameListener that will be notified when a change in the model occurs.
        // After being notified the Listener will send a message to the client containing the event and the ModelView obj
        eventManager.subscribe(GameEvent.class, new GameListener(this, client, this.getGame()));
    }

    /**
     * Register a client to the server using RMI
     * @param rmiClientInterface the client to register (as an RMI interface)
     */
    @Override
    public void register(ClientRMIInterface rmiClientInterface) {
        try {
            Client client = new Client(rmiClientInterface);
            this.register(client);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Unregister a client from the server
     * @param client the client to unregister
     */
    public void unregister(Client client) {
        this.clients.remove(client);
        // eventManager.unsubscribe(GameEvent.class, new GameListener(this, client));
        //The above is not needed I think
    }

    /**
     * Update of the server after a client send a message. This method forwards the message produced by the View (which is
     * observed by the client) to the controller, specifying the client that generated the event.
     * @param client  the client that generated the event
     * @param msg wrapped message received from the client
     */
    @Override
    public EventWrapper update(Client client, CommandMessageWrapper msg) {
        if (!clients.contains(client)) {
            throw new IllegalArgumentException("Client not registered");
        }
        // TODO: understand how to use information about the client that sent the message

        // unwrap the message
        UserInputEvent messageType = msg.getType();
        String messageCommand = msg.getMessage();
        // call the update on the controller
        try {
            this.controller.executeCommand(messageCommand, messageType);
        } catch (WrongTurnException | InvalidCommand | WrongArgumentException e) {
            return new EventWrapper(e.getMessage(), GameEvent.ERROR);
        }
        return new EventWrapper(null, GameEvent.RESPONSE_OK);
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
        try {
            // Create the RMI registry
            LocateRegistry.createRegistry(1099);
        } catch (ExportException e) {
            // If the registry already exists, it will throw an exception.
            // In this case, we get the registry and continue
            LocateRegistry.getRegistry(1099);
        }
        // Bind the server object to the registry
        Naming.rebind("//localhost/" + RMI_SERVER_NAME, this);
        System.out.println("Server started with RMI.");
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
                System.out.println("Server started with sockets.");
                lock.notifyAll();
            }

            // Loop to handle multiple connections
            while (true) {
                Socket clientSocket = null;
                try {
                    // Accept a new client connection
                    clientSocket = serverSocket.accept();
                    System.out.println("Accepted new socket connection.");
                } catch (SocketException e) {
                    System.out.println("Socket closed.");
                    return;
                }

                //Get client nickname
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String nickname = input.readLine();

                //Create and register a new client
                Client client = new Client(nickname);
                client.setClientSocket(clientSocket);
                this.register(client);
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
            Naming.unbind("//localhost/" + RMI_SERVER_NAME);
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
    public void sendTo(Socket clientSocket, EventWrapper ew) {
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            output.writeObject(ew);
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
                // Create a new input stream to read from the client socket
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Loop to handle multiple client requests
                while (true) {
                    // Read a request from the client
                    String request = input.readLine();
                    // If the request is null, the client has disconnected
                    if (request == null) {
                        //TODO handle disconnection
                        System.out.println("Client disconnected.");
                        break;
                    }

                    // Handle the request
                    EventWrapper response = Server.this.update(this.client, new CommandMessageWrapper(request));
                    Server.this.sendTo(clientSocket, response);
                }

                // Close the client socket and unregister the client
                clientSocket.close();
                Server.this.clients.remove(client);
            } catch (IOException e) {
                System.err.println("Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}

