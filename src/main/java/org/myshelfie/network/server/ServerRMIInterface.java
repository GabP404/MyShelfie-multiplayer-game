package org.myshelfie.network.server;

import org.myshelfie.network.client.Client;
import org.myshelfie.network.client.ClientRMIInterface;
import org.myshelfie.network.messages.commandMessages.CommandMessageWrapper;
import org.myshelfie.network.messages.commandMessages.CreateGameMessage;
import org.myshelfie.network.messages.commandMessages.JoinGameMessage;
import org.myshelfie.network.messages.gameMessages.EventWrapper;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface ServerRMIInterface extends Remote {
    /**
     * Update of the server after a client has made a choice
     * @param client  the client that generated the event
     * @param msg wrapped message received from the client
     */
    void update(Client client, CommandMessageWrapper msg) throws RemoteException;

    /**
     * Register a client to the server
     * @param client the client to register
     */
    void register(ClientRMIInterface client) throws RemoteException;

    /**
     * Creates a new game on the server and returns its identifier
     * @param client Client that wants to create the game
     * @param message Message containing the game settings
     * @return ID of the game
     * @throws RemoteException if it's impossible to create the game
     */
    String createGame(CreateGameMessage message) throws RemoteException;

    /**
     * Joins an existing game on the server
     * @param client Client that wants to join the game
     * @param message Message containing the game identifier
     * @return ID of the game
     * @throws RemoteException if the game is already full or if the game doesn't exist
     */
    String joinGame(JoinGameMessage message) throws RemoteException;
}
