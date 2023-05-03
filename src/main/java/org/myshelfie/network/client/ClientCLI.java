package org.myshelfie.network.client;

import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.messages.gameMessages.GameView;
import org.myshelfie.view.CommandLineInterface;

import java.rmi.RemoteException;

public class ClientCLI extends Client implements Runnable {
    private CommandLineInterface view;

    /**
     * Create a new client and register it to the server, establish a listening relationship between the view and the server
     * @param nickName the nickname of the client
     * @param isRMI true if the client is an RMI client, false if it is a socket client
     */
    public ClientCLI(String nickName, boolean isRMI) throws RemoteException {
        super(nickName, isRMI);
        view = new CommandLineInterface(nickName);
    }

    /**
     * Update of the client after the GameView has been modifies (or an Error has been received)
     * @param argument GameView in case of a model change, String in case of an error
     * @param ev Type of the information received
     */
    @Override
    public void update(Object argument, GameEvent ev) {
        if (ev == GameEvent.ERROR) {
            //TODO handle error
        }
        else {
            GameView v = (GameView) argument;
            view.update(v, ev);
        }
    }

    @Override
    public void run() {
        view.run();
    }
}
