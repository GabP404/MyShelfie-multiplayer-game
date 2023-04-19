package org.myshelfie.network.server;

import org.myshelfie.network.client.Client;
import org.myshelfie.network.client.ClientRMIInterface;
import org.myshelfie.network.messages.commandMessages.CommandMessageWrapper;
import org.myshelfie.network.messages.gameMessages.EventWrapper;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerRMIInterface extends Remote {
    /**
     * Update of the server after a client has made a choice
     * @param client  the client that generated the event
     * @param msg wrapped message received from the client
     */
    EventWrapper update(Client client, CommandMessageWrapper msg) throws RemoteException;

    /**
     * Register a client to the server
     * @param client the client to register
     */
    void register(ClientRMIInterface client) throws RemoteException;
}
