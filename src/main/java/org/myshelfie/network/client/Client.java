package org.myshelfie.network.client;

import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.messages.gameMessages.GameView;
import org.myshelfie.view.CommandLineInterface;

public abstract class Client {
    private CommandLineInterface view;
    /**
     * Called by the server to propagate the model change (observed by the modelView) to the view
     * @param o    The resulting model view
     * @param ev   The causing event
     */
    public abstract void update(GameView o, GameEvent ev);

    /**
     * This method is needed for make the CLI available to the UserInputListener that will have to retrieve changes
     * made by the client on the view and send them to the server
     * @return The view of this client
     */
    CommandLineInterface getCLI() {
        return this.view;
    }
}
