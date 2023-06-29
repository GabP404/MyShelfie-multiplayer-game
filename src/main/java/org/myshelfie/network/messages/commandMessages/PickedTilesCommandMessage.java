package org.myshelfie.network.messages.commandMessages;

import org.myshelfie.model.LocatedTile;
import org.myshelfie.model.util.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents the command used to send the choice of the tiles picked from the Board.
 */
public class PickedTilesCommandMessage extends CommandMessage implements Serializable {
    private final List<Pair<Integer, Integer>> tiles;

    /**
     * @param nickname Nickname of the player sending the message
     * @param tiles    List of the chosen tiles. Note that they're objects of type {@link LocatedTile}.
     *                 This allows to keep information about the tiles position in the board.
     */
    public PickedTilesCommandMessage(String nickname, String gameName, List<LocatedTile> tiles) {
        super(nickname, gameName);
        this.tiles = tiles.stream().map(t -> new Pair<>(t.getRow(), t.getCol())).toList();
    }

    /**
     * @return The list containing the coordinates of the selected tiles in the board.
     */
    public List<Pair<Integer, Integer>> getTiles() {
        return tiles;
    }
}
