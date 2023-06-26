package org.myshelfie.network.client;

import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.messages.gameMessages.GameView;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface implemented by a client to receive updates from the server using Remote Method Invocation.
 */
public interface ClientRMIInterface extends Remote {
    /**
     * Update of the client after the GameView has been modifies (or an Error has been received)
     * @param argument GameView in case of a model change, String in case of an error
     * @param ev Type of the information received
     */
    void update(GameView argument, GameEvent ev) throws RemoteException;

    /**
     * Returns the nickname of the client
     * @return Nickname of the client
     */
    String getNickname() throws RemoteException;
}
