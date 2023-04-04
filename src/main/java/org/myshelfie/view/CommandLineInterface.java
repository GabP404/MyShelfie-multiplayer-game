package org.myshelfie.view;

import org.myshelfie.model.Game;
import org.myshelfie.model.GameView;
import org.myshelfie.model.LocatedTile;
import org.myshelfie.network.CommandMessageType;
import org.myshelfie.util.Observable;

import java.util.List;

public class CommandLineInterface extends Observable<CommandMessageType> implements Runnable {
    private final String nickname;
    private List<LocatedTile> selectedTiles;    // tiles selected from the board
    private int selectedColumn;     // the column into insert a new hand of tiles
    private int tileIndex;          // during tiles insertion, the index inside the hand of the tile to insert
    private String chatMessage;
    private String whispNickname;
    // TODO: add attribute for chat message log

    public CommandLineInterface(String nickname) {
        this.nickname = nickname;
        this.selectedTiles = null;
        this.selectedColumn = -1;
        this.tileIndex = -1;
        this.chatMessage = null;
        this.whispNickname = null;
    }

    @Override
    public void run() {

    }

    public void update(GameView o, Game.Event arg) {

    }

}
