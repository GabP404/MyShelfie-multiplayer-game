package org.myshelfie.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.myshelfie.network.messages.gameMessages.ImmutableBookshelf;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImmutableBookshelfTest {
    // NOTE: since these tests address an immutable class, only observable behavior is tested
    private static ImmutableBookshelf b;

    @BeforeAll
    public static void setUp() {
        Bookshelf shelf = new Bookshelf();
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
            b = new ImmutableBookshelf(shelf);

        } catch (WrongArgumentException e) {
            fail();
        }


    }

    @Test
    public void testAdjacentTilesSize() {
        List<Integer> groupSizes = b.getAdjacentSizes();
        Integer[] expectedResults = {6, 6, 5, 3, 1};
        assertEquals(groupSizes.size(), 6);
        assertTrue(groupSizes.containsAll(List.of(expectedResults)));
    }

    @Test
    public void testGetTile() throws WrongArgumentException {
        Tile t = b.getTile(5,0);
        assertNotNull(t);
        assertEquals(t.getItemType(),ItemType.CAT);
    }
    @Test
    public void testGetColumnsHeight() {
        assertEquals(b.getHeight(0),5);
        assertEquals(b.getHeight(1),4);
        assertEquals(b.getHeight(2),3);
        assertEquals(b.getHeight(3),5);
        assertEquals(b.getHeight(4),5);
    }


}