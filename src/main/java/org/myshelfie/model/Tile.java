package org.myshelfie.model;

import java.io.Serializable;

public class Tile  implements Serializable {
    private ItemType itemType;
    private int itemId;


    public Tile(ItemType itemType) {
        this.itemType = itemType;
        this.itemId = 1;
    }

    public Tile(Tile t) {
        this.itemType = t.getItemType();
        this.itemId = t.getItemId();
    }

    public Tile(ItemType itemType, int itemId) {
        this.itemType = itemType;
        this.itemId = itemId;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public int getItemId() { return itemId; }
}