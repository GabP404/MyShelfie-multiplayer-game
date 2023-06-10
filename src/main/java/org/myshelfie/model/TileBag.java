package org.myshelfie.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.myshelfie.controller.Configuration;

public class TileBag implements Serializable {
    private static final int TILEPERTYPE = Configuration.getTilesPerType();
    private List<Tile> tiles;


    public TileBag() {
        this.tiles = new ArrayList<>();

        for (ItemType t : ItemType.values()) {
            for (int i = 0; i < TILEPERTYPE; i++) {
                this.tiles.add(new Tile(t,(i%3) + 1));
            }
        }
        Collections.shuffle(this.tiles);
    }

    /**
     * Method that returns the first tile in the bag and removes it from the bag.
     * @return The first tile in the bag
     */
    public Tile drawItemTile() throws WrongArgumentException{
        if (tiles.size() == 0)
            throw new WrongArgumentException("The bag is empty!");
        return tiles.remove(0);
    }

    /**
     * Method that returns the size of the bag.
     * @return The size of the bag
     */
    public int getSize() {
        return tiles.size();
    }

}
