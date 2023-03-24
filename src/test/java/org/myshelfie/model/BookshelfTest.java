package org.myshelfie.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Bookshelf")
class BookshelfTest {
    private Bookshelf shelf;

    @BeforeEach
    void setUp() {
        shelf = new Bookshelf();
    }

    @AfterEach
    void tearDown() {
        shelf = null;
    }

    @Test
    void insertTile() {
        assertDoesNotThrow(() -> {
            for(int i = 0; i < Bookshelf.NUMROWS; i++) {
                Tile t = new Tile(ItemType.values()[0]);
                shelf.insertTile(t, 0);
            }
        });
        assertThrows(TileInsertionException.class, () -> {
            Tile t = new Tile(ItemType.values()[0]);
            shelf.insertTile(t, 0);
        });
    }

    @DisplayName("Getting a tile in an empty bookshelf")
    @Test
    void getTileWithEmptyBookshelf() {
        assertDoesNotThrow(() -> {
            for(int i = 0; i < Bookshelf.NUMROWS; i++) {
                for (int j = 0; j < Bookshelf.NUMCOLUMNS; j++) {
                    assertNull(shelf.getTile(i, j));
                }
            }
        });
    }
    
    @DisplayName("Getting a tile out of bounds")
    @Test
    void getUnreachableTile() {
        assertThrows(TileUnreachableException.class, () -> shelf.getTile(Bookshelf.NUMROWS + 1, 0));
        assertThrows(TileUnreachableException.class, () -> shelf.getTile(0, Bookshelf.NUMCOLUMNS + 1));
    }
    
    @DisplayName("Getting a tile in normal conditions")
    @Test
    void getTile() {
        try {
            shelf.insertTile(new Tile(ItemType.values()[0]), 0);
            shelf.insertTile(new Tile(ItemType.values()[0]), 0);
            shelf.insertTile(new Tile(ItemType.values()[0]), 0);
            Tile t = shelf.getTile(3, 0);
            assertInstanceOf(Tile.class, t);
            assertEquals(t.getItemType(), ItemType.values()[0]);
        } catch (TileInsertionException | TileUnreachableException e) {
            fail();
        }
    }

    @Test
    void getHeight() {
        for (int i = 0; i < Bookshelf.NUMCOLUMNS; i++) {
            assertEquals(shelf.getHeight(i), 0);
        }
        try {
            shelf.insertTile(new Tile(ItemType.values()[0]), 0);
            shelf.insertTile(new Tile(ItemType.values()[0]), 0);
            shelf.insertTile(new Tile(ItemType.values()[0]), 0);
            shelf.insertTile(new Tile(ItemType.values()[0]), 2);
            shelf.insertTile(new Tile(ItemType.values()[0]), 2);
            assertEquals(shelf.getHeight(0), 3);
            assertEquals(shelf.getHeight(1), 0);
            assertEquals(shelf.getHeight(2), 2);
            assertEquals(shelf.getHeight(3), 0);
        } catch (TileInsertionException e) {
            fail();
        }
    }

    @Test
    void getAdjacentSizes() {
        /*Create this bookshelf:
        - - - - -
        C - - B B
        C C - B B
        C P P P B
        C P P B P
        C P B B C
         */
        try {
            //Column 0
            shelf.insertTile(new Tile(ItemType.CAT), 0);
            shelf.insertTile(new Tile(ItemType.CAT), 0);
            shelf.insertTile(new Tile(ItemType.CAT), 0);
            shelf.insertTile(new Tile(ItemType.CAT), 0);
            shelf.insertTile(new Tile(ItemType.CAT), 0);

            //Column 1
            shelf.insertTile(new Tile(ItemType.PLANT), 1);
            shelf.insertTile(new Tile(ItemType.PLANT), 1);
            shelf.insertTile(new Tile(ItemType.PLANT), 1);
            shelf.insertTile(new Tile(ItemType.CAT), 1);

            //Column 2
            shelf.insertTile(new Tile(ItemType.BOOK), 2);
            shelf.insertTile(new Tile(ItemType.PLANT), 2);
            shelf.insertTile(new Tile(ItemType.PLANT), 2);

            //Column 3
            shelf.insertTile(new Tile(ItemType.BOOK), 3);
            shelf.insertTile(new Tile(ItemType.BOOK), 3);
            shelf.insertTile(new Tile(ItemType.PLANT), 3);
            shelf.insertTile(new Tile(ItemType.BOOK), 3);
            shelf.insertTile(new Tile(ItemType.BOOK), 3);

            //Column 4
            shelf.insertTile(new Tile(ItemType.CAT), 4);
            shelf.insertTile(new Tile(ItemType.PLANT), 4);
            shelf.insertTile(new Tile(ItemType.BOOK), 4);
            shelf.insertTile(new Tile(ItemType.BOOK), 4);
            shelf.insertTile(new Tile(ItemType.BOOK), 4);

            List<Integer> groupSizes = shelf.getAdjacentSizes();
            Integer[] expectedResults = {6, 6, 5, 3, 1};
            assertEquals(groupSizes.size(), 6);
            assertTrue(groupSizes.containsAll(List.of(expectedResults)));
        } catch (TileInsertionException e) {
            fail();
        }
    }
}