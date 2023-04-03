package org.myshelfie.network;

import org.myshelfie.model.Game;
import org.myshelfie.model.GameView;

public interface Client {
    /**
     * Called by the server to propagate the model change (observed by the modelView) to the view
     * @param o    The resulting model view
     * @param ev   The causing event
     */
    void update(GameView o, Game.Event ev);
}
