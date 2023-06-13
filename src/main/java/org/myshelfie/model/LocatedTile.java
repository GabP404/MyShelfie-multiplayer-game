package org.myshelfie.model;

import org.myshelfie.model.util.Pair;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class extends Tile adding the information about the location of the tile in the bookshelf.
 */
public class LocatedTile extends Tile implements Serializable {
    private int row;
    private int col;

    /**
     * Constructor for the LocatedTile class
     * @param itemType Type of the Tile
     * @param r The row in the board
     * @param c The column in the board
     */
    public LocatedTile(ItemType itemType, int r, int c) {
        super(itemType);
        row = r;
        col = c;
    }

    public LocatedTile(ItemType itemType, int id, int r, int c) {
        super(itemType, id);
        row = r;
        col = c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LocatedTile that = (LocatedTile) o;
        return row == that.row &&
                col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), row, col);
    }


    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Pair<Integer, Integer> getCoordinates() {
        return new Pair<>(row, col);
    }
}
