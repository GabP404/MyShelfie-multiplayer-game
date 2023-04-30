package org.myshelfie.network.server;

import org.myshelfie.controller.GameController;
import org.myshelfie.model.Game;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.EventManager;
import org.myshelfie.network.messages.commandMessages.CommandMessageWrapper;
import org.myshelfie.network.messages.commandMessages.UserInputEvent;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.messages.gameMessages.WrongInputEvent;

import java.util.ArrayList;
import java.util.List;

public class ServerImpl implements Server {
    private List<Client> clients;
    private GameController controller;
    public static EventManager eventManager = new EventManager();
    private Game model;


    /**
     * Constructor of the server, it creates the model and the controller for the game.
     * NOTE: for the moment the server can only handle one Game (but it can handle multiple clients).
     */
    public ServerImpl() {
        // FIXME: Game inizialization missing
        /*
        this.model = new Game();
        this.controller = new GameController(model, clients);
        this.clients = new ArrayList<>(); */
    }

    /**
     * Overloaded constructor used during testing since it allows to initialize the Game object outside
     * @param game Already initialized model
     */
    public ServerImpl(Game game) {
        this.model = game;
        this.clients = new ArrayList<>();
    }

    /**
     * Register a client to the server
     * @param client the client to register
     * // TODO: to handle multiple games this method will need a Game game parameter
     *          which will be used to create the {@link GameListener#GameListener GameListener}
     */
    @Override
    public void register(Client client) {
        this.clients.add(client);
        // Subscribe a new GameListener that will be notified when a change in the model occurs.
        // After being notified the Listener will send a message to the client containing the event and the ModelView obj
        // TODO: to handle multiple games, the registration will need to refer to a specific game
        eventManager.subscribe(GameEvent.class, new GameListener(this, client, this.model));
        eventManager.subscribe(WrongInputEvent.class, new WrongInputListener(this, client));
    }

    /**
     * Update of the server after a client send a message. This method forwards the message produced by the View (which is
     * observed by the client) to the controller, specifying the client that generated the event.
     * @param client  the client that generated the event
     * @param msg wrapped message received from the client
     */
    @Override
    public void update(Client client, CommandMessageWrapper msg) {
        if (!clients.contains(client)) {
            throw new IllegalArgumentException("Client not registered");
        }
        // TODO: understand how to use information about the client that sent the message

        // unwrap the message
        UserInputEvent messageType = msg.getType();
        String messageCommand = msg.getMessage();
        // call the update on the controller
        this.controller.executeCommand(messageCommand, messageType);
    }
}

