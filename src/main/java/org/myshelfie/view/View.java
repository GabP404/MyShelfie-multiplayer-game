package org.myshelfie.view;

import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.messages.gameMessages.GameView;

public interface View {

    void update(GameView msg, GameEvent ev);
}
