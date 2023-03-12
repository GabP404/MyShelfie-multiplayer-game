package org.myshelfie.model;

public class Bookshelf {
    private Tile[][] tiles;

    /**
     * Bookshelf constructor.
     * It initializes an empty bookshelf.
     */
    public Bookshelf() {
        tiles = new Tile[6][5];
        for (int i = 0; i < 6; i++) {
           for (int j = 0; j < 5; j++) {
               tiles[i][j] = null;
           }
        }
    }

    /**
     * Add a tile in a specific column of the bookshelf, if possible.
     * @param t The tile
     * @param c The index of the column (0 <= c < 5)
     * @throws TileInsertionException if the column is already full
     */
    public void insertTile(Tile t, int c) throws TileInsertionException {
        if (tiles[0][c] != null)
            throw new TileInsertionException("This column is already full!");

        int i = 5;
        while (tiles[i][c] != null) {
            i--;
        }
        tiles[i][c] = t;
    }
}
