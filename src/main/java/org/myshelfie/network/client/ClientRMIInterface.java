package org.myshelfie.network.client;

import org.myshelfie.network.messages.commandMessages.CreateGameMessage;
import org.myshelfie.network.messages.commandMessages.JoinGameMessage;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.messages.gameMessages.GameView;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface ClientRMIInterface extends Remote {
    /**
     * Update of the client after the GameView has been modifies (or an Error has been received)
     * @param argument GameView in case of a model change, String in case of an error
     * @param ev Type of the information received
     */
    void update(GameView argument, GameEvent ev) throws RemoteException;

    /**
     * Returns a Client instance that can be registered on the server
     * @return
     * @throws RemoteException
     */
    Client getClientInstance() throws RemoteException;

    /**
     * Returns the nickname of the client
     * @return Nickname of the client
     */
    String getNickname() throws RemoteException;
}
