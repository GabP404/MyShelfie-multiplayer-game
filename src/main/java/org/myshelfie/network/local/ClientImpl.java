package org.myshelfie.network.local;

import org.myshelfie.model.Game;
import org.myshelfie.model.GameView;
import org.myshelfie.network.Client;
import org.myshelfie.network.CommandMessageWrapper;
import org.myshelfie.network.Server;
import org.myshelfie.view.CommandLineInterface;

public class ClientImpl implements Client, Runnable {
    CommandLineInterface view = new CommandLineInterface();

    /**
     * Create a new client and register it to the server, establish the observer relationship between the view and the server
     * @param server
     */
    public ClientImpl(Server server) {
        // Register the client to the server
        server.register(this);
        // Create an observer for the view that calls the server update method when the view is changed
        // forwarding the corresponding message
        // !!! TODO: since the observables can only notify the observers of the kind of event (in this case CommandMessageType),
        //           we have to handle here the creation of the CommandMessageWrapper message that will be sent to the server.
        view.addObserver((o, ev) -> server.update(this, new CommandMessageWrapper(msg, ev)));
    }

    /**
     * Called by the server to propagate the model change (observed by the modelView) to the view
     * @param o    The resulting model view
     * @param ev   The causing event
     */
    @Override
    public void update(GameView o, Game.Event ev) {
        view.update(o, ev);
    }

    @Override
    public void run() {
        view.run();
    }
}
