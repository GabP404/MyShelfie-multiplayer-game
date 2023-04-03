package org.myshelfie.view;

import org.myshelfie.model.Game;
import org.myshelfie.model.GameView;
import org.myshelfie.network.CommandMessageType;
import org.myshelfie.util.Observable;

public class CommandLineInterface extends Observable<CommandMessageType> implements Runnable {


    @Override
    public void run() {

    }

    public void update(GameView o, Game.Event arg) {

    }
}
