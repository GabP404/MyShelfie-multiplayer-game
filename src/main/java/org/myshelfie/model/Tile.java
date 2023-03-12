package org.myshelfie.model;

public class Tile {
    private ItemType itemType;


    public Tile(ItemType itemType) {
        this.itemType = itemType;
    }

    public ItemType getItemType() {
        return itemType;
    }
}
