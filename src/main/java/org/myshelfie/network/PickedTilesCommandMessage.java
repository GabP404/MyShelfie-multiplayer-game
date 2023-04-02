package org.myshelfie.network;

import org.json.JSONArray;
import org.json.JSONObject;
import org.myshelfie.model.LocatedTile;
import org.myshelfie.model.util.Pair;

import java.util.List;

/**
 * This class represents the command used to send the choice of the tiles picked from the Board
 */
public class PickedTilesCommandMessage extends CommandMessage {
    private final List<Pair<Integer, Integer>> tiles;

    /**
     * @param tiles List of the chosen tiles
     */
    public PickedTilesCommandMessage(List<LocatedTile> tiles) {
        this.tiles = tiles.stream().map(t -> new Pair<Integer, Integer>(t.getRow(), t.getCol())).toList();
    }

    @Override
    public String toString() {
        JSONArray complete = new JSONArray();
        for (Pair<Integer, Integer> coordinates: tiles) {
            JSONObject c = new JSONObject();
            c.put("row", coordinates.getLeft());
            c.put("col", coordinates.getRight());
            complete.put(c);
        }

        return complete.toString();
    }
}
