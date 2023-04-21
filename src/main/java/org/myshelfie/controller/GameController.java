package org.myshelfie.controller;

import org.myshelfie.model.Game;
import org.myshelfie.model.TileInsertionException;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.commandMessages.UserInputEventType;

import java.util.List;

public class GameController {
    private Game game;
    private List<Client> clients;
    public GameController() {
        game = new Game();
    }

    public GameController(Game game) {
        this.game = game;
    }

    public GameController(Game game, List<Client> clients) {
        this.game = game;
        this.clients = clients;
    }

    public void executeCommand(String command, UserInputEventType t) {
        try {
            Command c = null;
            switch (t) {
                case SELECTED_BOOKSHELF_COLUMN -> c = new PickTilesCommand(game.getBoard(),game.getCurrPlayer() ,command);
                case SELECTED_TILES -> c = new SelectTileFromHandCommand(game.getCurrPlayer(), command);
                case SELECTED_HAND_TILE -> c = new SelectColumnCommand(game.getCurrPlayer(), command);
            }
            c.execute();
        } catch (InvalidCommand e) {
            //TODO handle exception
        } catch (TileInsertionException e) {
            throw new RuntimeException(e);
        }
    }
}
