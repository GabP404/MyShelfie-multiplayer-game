package org.myshelfie.network.messages.commandMessages;

import org.myshelfie.model.LocatedTile;
import org.myshelfie.model.util.Pair;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * This class represents the command used to send the choice of the tiles picked from the Board
 */
public class PickedTilesCommandMessage extends CommandMessage implements Serializable  {

    private final List<Pair<Integer, Integer>> tiles;

    /**
     * @param nickname    Nickname of the player sending the message
     * @param tiles       List of the chosen tiles
     */
    public PickedTilesCommandMessage(String nickname, UUID gameUUID, List<LocatedTile> tiles) {
        super(nickname, gameUUID);
        this.tiles = tiles.stream().map(t -> new Pair<Integer, Integer>(t.getRow(), t.getCol())).toList();
    }

    public List<Pair<Integer, Integer>> getTiles() {
        return tiles;
    }
}
