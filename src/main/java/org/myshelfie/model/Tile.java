package org.myshelfie.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Class representing a Tile.
 */
public class Tile implements Serializable {
    private final ItemType itemType;
    private final int itemId;


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

    /**
     * @return The type of the tile.
     */
    public ItemType getItemType() {
        return itemType;
    }

    /**
     * @return The id of this tile, used to distinguish different tiles of the same type.
     */
    public int getItemId() { return itemId; }

    public boolean equals(Tile t) {
        return this.itemType.equals(t.getItemType()) && this.itemId == t.getItemId();
    }

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