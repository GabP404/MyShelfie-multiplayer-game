package org.myshelfie.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TileBag {
    private List<Tile> tiles;


    public TileBag() {
        this.tiles = new ArrayList<Tile>();

        for (ItemType t : ItemType.values()) {
            for (int i = 0; i < 22; i++) {
                this.tiles.add(new Tile(t));
            }
        }
        Collections.shuffle(this.tiles);
    }

    public Tile drawItemTile() {
        return tiles.remove(0);
    }

}
