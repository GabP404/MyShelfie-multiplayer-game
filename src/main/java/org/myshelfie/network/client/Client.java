package org.myshelfie.network.client;

import org.myshelfie.network.EventManager;
import org.myshelfie.network.messages.commandMessages.CommandMessageWrapper;
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
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements ClientRMIInterface, Runnable{

    protected String nickname;
    protected static final String SERVER_ADDRESS = "localhost";
    protected static final int SERVER_PORT = 1234;

    protected static String RMI_SERVER_NAME = "MinecraftServer";
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

    public Client(boolean isRMI, boolean isGUI) throws RemoteException {
        this.isRMI = isRMI;
        if (isGUI) {
            // TODO: implement GUI
        } else {
            this.view = new ViewCLI(this);
        }

        // connect
        if (isRMI) {
            try {
                // Look up the server object in the RMI registry
                rmiServer = (ServerRMIInterface) Naming.lookup("//" + SERVER_ADDRESS + "/" + RMI_SERVER_NAME);
            } catch (Exception e) {
                System.err.println("Exception: " + e.getMessage());
                e.printStackTrace();
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
        // Subscribe a new UserInputListener that listen to changes in the view and forward events adding message to the server
        eventManager.subscribe(UserInputEvent.class, new UserInputListener(this));
    }


    public void endNicknameThread() {
        view.endNicknameThread();
    }

    public void endCreateGameThread() {
        view.endCreateGameThread();
        if (!isRMI) {
            try {
                serverListener.start();
            } catch (java.lang.IllegalThreadStateException e) {
                // Should never be thrown, but if thrown, then thread was already started!
            }
        }
    }
    public void endJoinGameThread() {
        view.endJoinGameThread();
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
                throw new RuntimeException(e);
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
