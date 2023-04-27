package org.myshelfie.network.server;

import org.myshelfie.model.Game;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.commandMessages.CommandMessageWrapper;

public abstract class Server {
    Game model;
    /**
     * Register a client to the server
     * @param client the client to register
     */
    public abstract void register(Client client);

    /**
     * Getter for the model that the server is using. This method is used in order to allow GameListener to send
     * the updated modelView everytime a change occurs in the model.
     * NOTE: this method will need to be parametric when we'll handle multiple games.
     * @return The model used by the server
     */
    Game getGame() {
        return model;
    }

    /**
     * Update of the server after a client has made a choice
     * @param client  the client that generated the event
     * @param msg wrapped message received from the client
     */
    public abstract void update(Client client, CommandMessageWrapper msg);
}
