package org.myshelfie.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TileTest {

    @Test
    public void testGetItemType() {
        Tile tile = new Tile(ItemType.CAT);
        Assertions.assertEquals(ItemType.CAT, tile.getItemType());
    }

    @Test
    public void testGetItemId() {
        Tile tile = new Tile(ItemType.CAT, 1);
        Assertions.assertEquals(1, tile.getItemId());
    }

    @Test
    public void testTileConstructor() {
        Tile tile = new Tile(ItemType.BOOK, 1);
        Assertions.assertNotNull(tile);
        Assertions.assertEquals(ItemType.BOOK, tile.getItemType());
        Assertions.assertEquals(1, tile.getItemId());
    }
}
