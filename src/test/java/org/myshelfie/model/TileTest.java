package org.myshelfie.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.myshelfie.model.util.Pair;

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

        Tile tile2 = new Tile(tile);
        Assertions.assertNotNull(tile2);
        Assertions.assertEquals(tile2, tile);
    }

    @Test
    public void testLocatedTiles() {
        LocatedTile lt = new LocatedTile(ItemType.BOOK, 0, 0);
        LocatedTile lt2 = new LocatedTile(ItemType.BOOK, 0, 0);
        Assertions.assertNotNull(lt);
        Assertions.assertEquals(ItemType.BOOK, lt.getItemType());
        Assertions.assertTrue(lt.equals(lt2));
        Assertions.assertEquals(lt.getCoordinates(), new Pair<Integer, Integer>(0, 0));
    }
}
