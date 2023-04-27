package org.myshelfie.controller;

import org.json.JSONObject;
import org.myshelfie.model.*;

import java.util.List;

public class SelectTileFromHandCommand implements Command {
    private final String nickname;
    private Player player;
    private final int index;
    //private final List<Tile> hand;
    private final ItemType itemType;

    public SelectTileFromHandCommand(Player currPlayer, String serial) {
        player = currPlayer;
        JSONObject jo = new JSONObject(serial);
        nickname = jo.getString("nickname");
        index = jo.getInt("index");
        itemType = (ItemType) jo.get("itemType");
    }

    public void execute() throws InvalidCommand, TileInsertionException {
        if(!player.getNickname().equals(nickname))
        {
            //TODO: maybe handle wrong turn command
            return;
        }

        if (index >= player.getTilesPicked().size() || index < 0) {
            throw new InvalidCommand("");
        }

        if (player.getTilesPicked().get(index).getItemType() != itemType) {
            throw new InvalidCommand("The forecasted tile types do not match!");
        }

        if (player.getSelectedColumn() >= Bookshelf.NUMCOLUMNS || player.getSelectedColumn() < 0) {
            throw new InvalidCommand("Selected column is out of bound");
        }


        player.getBookshelf().insertTile(player.getTilesPicked().get(index), player.getSelectedColumn());
        player.removeTilesPicked(player.getTilesPicked().get(index));

        if(player.getTilesPicked().size() == 0)
        {
            player.setSelectedColumn(-1);
            //TODO: change state when implemented
        }

    }
}
