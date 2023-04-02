package org.myshelfie.network;

import org.json.JSONObject;
import org.myshelfie.model.ItemType;

/**
 * This class represents the command sent to the server to choose which tile the player wants to put in the
 * selected column (amongst the ones present in their hand)
 */
public class SelectedTileFromHandCommandMessage extends CommandMessage {
    private final int index;
    private final ItemType tileType;

    /**
     * @param index The index of the tile in the player's current hand
     * @param tileType The type of the tile (just to make sure)
     */
    public SelectedTileFromHandCommandMessage(int index, ItemType tileType) {
        this.index = index;
        this.tileType = tileType;
    }

    @Override
    public String toString() {
        JSONObject jo = new JSONObject();
        jo.put("index", index);
        jo.put("tileType", tileType);
        return jo.toString();
    }
}
