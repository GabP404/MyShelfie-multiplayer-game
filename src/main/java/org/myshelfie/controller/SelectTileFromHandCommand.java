package org.myshelfie.controller;

import org.myshelfie.model.ItemType;
import org.myshelfie.model.ModelState;
import org.myshelfie.model.Player;
import org.myshelfie.model.WrongArgumentException;
import org.myshelfie.network.messages.commandMessages.SelectedTileFromHandCommandMessage;

public class SelectTileFromHandCommand implements Command {
    private final String nickname;
    private Player currPlayer;
    private final int index;
    //private final List<Tile> hand;
    private final ItemType itemType;
    private ModelState currentModelState;

    public SelectTileFromHandCommand(Player currPlayer, SelectedTileFromHandCommandMessage command, ModelState currentModelState) {
        this.currPlayer = currPlayer;
        nickname = command.getNickname();
        index = command.getIndex();
        itemType = command.getTileType();
        this.currentModelState = currentModelState;
    }

    public void execute() throws InvalidCommand, WrongTurnException, WrongArgumentException {
        if(!currPlayer.getNickname().equals(nickname)) {
            throw new WrongTurnException("Wrong player turn");
        }
        if(currentModelState != ModelState.WAITING_3_SELECTION_TILE_FROM_HAND && currentModelState != ModelState.WAITING_2_SELECTION_TILE_FROM_HAND && currentModelState != ModelState.WAITING_1_SELECTION_TILE_FROM_HAND){
            throw new InvalidCommand("Waiting for Tile Selection Hand ");
        }

        currPlayer.getBookshelf().insertTile(currPlayer.getTilePicked(index), currPlayer.getSelectedColumn());
        currPlayer.removeTilesPicked(currPlayer.getTilePicked(index));
    }
}
