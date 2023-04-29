package org.myshelfie.model;

import org.junit.jupiter.api.Test;
import org.myshelfie.network.messages.gameMessages.ImmutableBoard;

import static org.junit.jupiter.api.Assertions.*;

class ImmutableBoardTest {

    @Test
    public void testConstructor() {
        Board board = new Board(4);
        ImmutableBoard x = new ImmutableBoard(board);
        assertNotNull(x);
        assertNotNull(x.getBoard());
        assertNull(x.getTile(0, 0));
    }

    @Test
    public void testRefillBoard() {
        TileBag bag = new TileBag();
        Board board = new Board(4);
        board.refillBoard(4, bag);
        ImmutableBoard x = new ImmutableBoard(board);
        assertNull(x.getTile(0, 0));
        assertNotNull(x.getTile(0, 3));
        assertNotNull(x.getTile(0, 4));
        assertNotNull(x.getTile(4, 4));
        assertNull(x.getTile(8, 8));
    }

    @Test
    public void testIsRefillNeeded() {
        TileBag bag = new TileBag();
        Board board = new Board(4);
        board.setTile(4,4,new Tile(ItemType.BOOK));
        board.setTile(4,5,new Tile(ItemType.BOOK));
        board.setTile(5,4,new Tile(ItemType.BOOK));
        board.setTile(5,5,new Tile(ItemType.BOOK));
        ImmutableBoard x = new ImmutableBoard(board);
        assertTrue(x.isRefillNeeded());

        board.setTile(4,4,null);
        board.setTile(4,5,null);
        board.setTile(5,4,null);
        board.setTile(5,5,null);
        board.refillBoard(4, bag);
        x = new ImmutableBoard(board);

        assertFalse(x.isRefillNeeded());
    }

}