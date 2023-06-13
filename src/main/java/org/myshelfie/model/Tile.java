package org.myshelfie.model;

import java.io.Serializable;
import java.util.Objects;

public class Tile implements Serializable {
    private ItemType itemType;
    private int itemId;


    public Tile(ItemType itemType) {
        this.itemType = itemType;
        this.itemId = 1;
    }


    public boolean equals(Tile t) {
        return this.itemType.equals(t.getItemType()) && this.itemId == t.getItemId();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return itemId == tile.itemId &&
                itemType == tile.itemType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemType, itemId);
    }
}