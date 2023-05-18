package org.myshelfie.controller;

import org.myshelfie.model.*;
import org.myshelfie.network.messages.commandMessages.SelectedTileFromHandCommandMessage;

import java.util.ArrayList;
import java.util.List;

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
            throw new WrongTurnException();
        }
        if(currentModelState != ModelState.WAITING_3_SELECTION_TILE_FROM_HAND && currentModelState != ModelState.WAITING_2_SELECTION_TILE_FROM_HAND && currentModelState != ModelState.WAITING_1_SELECTION_TILE_FROM_HAND){
            throw new InvalidCommand("Waiting for Tile Selection Hand ");
        }

        // Since the update message is sent to the player from the call to the notify method inside the insertTile method,
        // we need the tile to be already removed from the hand before calling the insertTile method.
        // To avoid removing the tile from the hand when an error occurs inside the insertTile method, we keep a copy of the
        // selected tiles and if necessary restore it.
        List<Tile> handCopy = new ArrayList<>(currPlayer.getTilesPicked());
        currPlayer.removeTilesPicked(currPlayer.getTilePicked(index));

        try {
            // NOTE: here we have to use the copy of the hand since the tile selected has already been removed from the player's hand
            currPlayer.getBookshelf().insertTile(handCopy.get(index), currPlayer.getSelectedColumn());
        } catch (WrongArgumentException e) {
            currPlayer.setTilesPicked(handCopy);
            throw new WrongArgumentException(e.getMessage());
        }
    }
}
