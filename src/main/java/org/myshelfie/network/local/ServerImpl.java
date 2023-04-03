package org.myshelfie.network.local;

import org.myshelfie.controller.GameController;
import org.myshelfie.model.Game;
import org.myshelfie.model.GameView;
import org.myshelfie.network.Client;
import org.myshelfie.network.CommandMessageType;
import org.myshelfie.network.CommandMessageWrapper;
import org.myshelfie.network.Server;

import java.util.ArrayList;
import java.util.List;

public class ServerImpl implements Server {
    private List<Client> clients;
    private Game model;
    private GameController controller;

    /**
     * Constructor of the server, it creates the model and the controller for the game.
     * NOTE: for the moment the server can only handle one Game (but it can handle multiple clients).
     */
    public ServerImpl() {
        this.model = new Game();
        this.controller = new GameController(model, clients);
        this.clients = new ArrayList<>();
    }

    /**
     * Register a client to the server
     * @param client the client to register
     */
    @Override
    public void register(Client client) {
        this.clients.add(client);
        // This code allows you to create an observer for the model "on the fly". In particular, we establish the
        // bound between the model (observable) and the server (observer) while saying that on a notification from the model,
        // the server will call the update() method of the client, passing as parameters a GameView and the event.
        // NOTE: when we'll start working with network communication the client update() will be a remote call.
        this.model.addObserver((o, ev) -> client.update(new GameView(model), ev));
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
        CommandMessageType messageType = msg.getType();
        String messagePayload = msg.getMessage();
        // call the update on the controller
        this.controller.executeCommand(messagePayload, messageType);
    }
}

