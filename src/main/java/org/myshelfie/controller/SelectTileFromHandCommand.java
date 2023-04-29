package org.myshelfie.controller;

import org.json.JSONObject;
import org.myshelfie.model.*;

public class SelectTileFromHandCommand implements Command {
    private final String nickname;
    private Player currPlayer;
    private final int index;
    //private final List<Tile> hand;
    private final ItemType itemType;
    private ModelState currentModelState;

    public SelectTileFromHandCommand(Player currPlayer, String serial, ModelState currentModelState) {
        this.currPlayer = currPlayer;
        JSONObject jo = new JSONObject(serial);
        nickname = jo.getString("nickname");
        index = jo.getInt("index");
        itemType = (ItemType) jo.get("itemType");
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
