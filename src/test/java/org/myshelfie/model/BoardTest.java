package org.myshelfie.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    @Test
    public void testConstructor() {
        Board board = new Board();
        assertNotNull(board);
        assertNull(board.getTile(0, 0));
    }

    @Test
    public void testRefillBoard() {
        TileBag bag = new TileBag();
        Board board = new Board(4);
        try {
            board.refillBoard(4, bag);
        } catch (WrongArgumentException e) {
            fail("Exception thrown");
        }
        assertNull(board.getTile(0, 0));
        assertNotNull(board.getTile(0, 3));
        assertNotNull(board.getTile(0, 4));
        assertNotNull(board.getTile(4, 4));
        assertNull(board.getTile(8, 8));
    }

    @Test
    public void testIsRefillNeeded() {
        TileBag bag = new TileBag();
        Board board = new Board(4);
        assertTrue(board.isRefillNeeded());

        board.setTile(4,4,new Tile(ItemType.BOOK));
        board.setTile(4,5,new Tile(ItemType.BOOK));
        board.setTile(5,4,new Tile(ItemType.BOOK));
        board.setTile(5,5,new Tile(ItemType.BOOK));
        assertTrue(board.isRefillNeeded());

        board.setTile(4,4,null);
        board.setTile(4,5,null);
        board.setTile(5,4,null);
        board.setTile(5,5,null);
        try {
            board.refillBoard(4, bag);
        } catch (WrongArgumentException e) {
            fail("Exception thrown");
        }
        assertFalse(board.isRefillNeeded());
    }

   @Test
    public void testRemoveTile() {
        Board board = new Board(4);
        board.setTile(4,4,new Tile(ItemType.BOOK));
        Tile t = board.removeTile(4,4);
        assert t != null;
    }

    @Test
    public void testSetAndGetTile() {
        Board board = new Board();
        Tile tile = new Tile(ItemType.BOOK);
        board.setTile(0, 0, tile);
        assertEquals(tile, board.getTile(0, 0));
    }


}
