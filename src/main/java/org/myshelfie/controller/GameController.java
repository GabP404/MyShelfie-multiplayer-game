package org.myshelfie.controller;

import org.myshelfie.model.Game;
import org.myshelfie.network.CommandMessageType;

public class GameController {
    private Game game;
    public GameController() {
        game = new Game();
    }

    public void executeCommand(String command, CommandMessageType t) {
        try {
            Command c = null;
            switch (t) {
                case SELECTED_BOOKSHELF_COLUMN -> c = new PickTilesCommand(game.getBoard(), command);
                case SELECTED_TILES -> c = new SelectTileFromHandCommand(game.getCurrPlayer(), command);
                case SELECTED_HAND_TILE -> c = new SelectColumnCommand(game.getCurrPlayer(), command);
                case CHAT_MESSAGE -> c = new ChatCommand(command);
            }
            c.execute();
        } catch (InvalidCommand e) {
            //TODO handle exception
        }
    }
}
