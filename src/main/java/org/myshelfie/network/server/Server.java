package org.myshelfie.network.server;

import org.myshelfie.model.Game;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.commandMessages.CommandMessageWrapper;

public interface Server {
    /**
     * Register a client to the server
     * @param client the client to register
     */
    public abstract void register(Client client);

    /**
     * Update of the server after a client has made a choice
     * @param client  the client that generated the event
     * @param msg wrapped message received from the client
     */
    public abstract String update(Client client, CommandMessageWrapper msg);
}
