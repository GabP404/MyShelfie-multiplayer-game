package org.myshelfie.model;

public class Tile {
    private ItemType itemType;
    private int itemId;


    public Tile(ItemType itemType) {
        this.itemType = itemType;
        this.itemId = 1;
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