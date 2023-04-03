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
     * @param nickname    Nickname of the player sending the message
     * @param tiles       List of the chosen tiles
     */
    public PickedTilesCommandMessage(String nickname, List<LocatedTile> tiles) {
        super(nickname);
        this.tiles = tiles.stream().map(t -> new Pair<Integer, Integer>(t.getRow(), t.getCol())).toList();
    }

    @Override
    public String toString() {
        JSONObject complete = new JSONObject();
        complete.put("nickname", nickname);
        JSONArray coords = new JSONArray();
        for (Pair<Integer, Integer> coordinates: tiles) {
            JSONObject c = new JSONObject();
            c.put("row", coordinates.getLeft());
            c.put("col", coordinates.getRight());
            coords.put(c);
        }
        complete.put("coordinates", coords);

        return complete.toString();
    }
}
