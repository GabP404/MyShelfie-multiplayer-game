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

    /**
     * Method called to stop the thread that handles the nickname choice
     */
    void endNicknameThread();
    void endCreateGameThread();
    void endJoinGameThread();

    /**
     * Returns the name of the game the user is in
     * @return
     */
    String getGameName();

    // Methods used for testing TODO: possibly remove
    void setNickname(String nickname);
}
