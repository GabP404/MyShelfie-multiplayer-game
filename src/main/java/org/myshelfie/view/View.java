package org.myshelfie.view;

import org.myshelfie.controller.GameController;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.messages.gameMessages.GameView;

import java.util.List;

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
     */
    String getGameName();

    GameView getGameView();

    // Methods used for testing TODO: possibly remove
    void setNickname(String nickname);

    void setAvailableGames(List<GameController.GameDefinition> availableGamesList);

    void setReconnecting(boolean reconnecting);
}
