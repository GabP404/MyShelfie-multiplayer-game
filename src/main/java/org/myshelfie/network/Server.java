package org.myshelfie.network;

import org.myshelfie.network.messages.commandMessages.CommandMessageWrapper;

public interface Server {
    /**
     * Register a client to the server
     * @param client the client to register
     */
    void register(Client client);

    /**
     * Update of the server after a client has made a choice
     * @param client  the client that generated the event
     * @param msg wrapped message received from the client
     */
    void update(Client client, CommandMessageWrapper msg);
}
