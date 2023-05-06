package org.myshelfie.view;

import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.messages.gameMessages.GameView;

public interface View extends Runnable {

    /**
     * Method called to show updated view
     * @param msg The GameView that represents the immutable version of the updated model
     * @param ev Event that caused the model's change
     */
    void update(GameView msg, GameEvent ev);
    void run();
}
