package org.myshelfie.view;

import org.myshelfie.model.LocatedTile;
import org.myshelfie.network.messages.gameMessages.GameEventType;
import org.myshelfie.network.messages.gameMessages.GameView;

import java.util.List;

public class CommandLineInterface implements Runnable {
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

    public void update(GameView msg, GameEventType ev) {
        System.out.println("Received from server the event " + ev + "signaling a change in the model!");
        System.out.println("    Message payload: " + msg);
    }

}
