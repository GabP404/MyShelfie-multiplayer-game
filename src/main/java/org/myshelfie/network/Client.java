package org.myshelfie.network;

import org.myshelfie.network.messages.gameMessages.GameEventType;
import org.myshelfie.network.messages.gameMessages.GameView;
import org.myshelfie.view.CommandLineInterface;

public interface Client {
    /**
     * Called by the server to propagate the model change (observed by the modelView) to the view
     * @param o    The resulting model view
     * @param ev   The causing event
     */
    void update(GameView o, GameEventType ev);

    // IMPORTANT TODO: currently the view is accessible by the Server, we need to refactor packages (separating client and server) and use friendly methods
    CommandLineInterface getCLI();
}
