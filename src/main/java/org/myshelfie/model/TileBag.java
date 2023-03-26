package org.myshelfie.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TileBag {
    private static final int TILEPERTYPE = 22;
    private List<Tile> tiles;


    public TileBag() {
        this.tiles = new ArrayList<Tile>();

        for (ItemType t : ItemType.values()) {
            for (int i = 0; i < TILEPERTYPE; i++) {
                this.tiles.add(new Tile(t,(i%3) + 1));
            }
        }
        Collections.shuffle(this.tiles);
    }

    public Tile drawItemTile() {
        return tiles.remove(0);
    }

    public int getSize() {
        return tiles.size();
    }

}
