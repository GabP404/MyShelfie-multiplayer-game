package org.myshelfie.controller;

import org.json.JSONObject;
import org.myshelfie.model.ItemType;
import org.myshelfie.model.Player;
import org.myshelfie.model.Tile;

import java.util.List;

public class SelectTileFromHandCommand implements Command {
    private final int index;
    private final List<Tile> hand;
    private final ItemType itemType;

    public SelectTileFromHandCommand(Player p, String serial) {
        hand = p.getTilesPicked();
        JSONObject jo = new JSONObject(serial);
        index = jo.getInt("index");
        itemType = (ItemType) jo.get("itemType");
    }

    public void execute() throws InvalidCommand {
        if (hand.get(index).getItemType() != itemType) {
            throw new InvalidCommand("The forecasted tile types do not match!");
        }
        //TODO idk maybe player.removeFromHand(index) (?)
    }
}
