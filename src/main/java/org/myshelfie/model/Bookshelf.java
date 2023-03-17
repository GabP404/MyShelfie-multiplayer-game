package org.myshelfie.model;

public class Bookshelf {

    private static final int DIMROW = 6;
    private static final int DIMCOLUMN = 5;

    private Tile[][] tiles;

    /**
     * Bookshelf constructor.
     * It initializes an empty bookshelf.
     */
    public Bookshelf() {
        tiles = new Tile[DIMROW][DIMCOLUMN];
        for (int i = 0; i < DIMROW; i++) {
           for (int j = 0; j < DIMCOLUMN; j++) {
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

        int i = DIMCOLUMN;
        while (tiles[i][c] != null) {
            i--;
        }
        tiles[i][c] = t;
    }

    //parameters are row(r) and column(c)
    public Tile getTile(int r, int c) throws TileUnreachableException {
        if(r < 0 || r >= DIMROW || c < 0 || c >= DIMCOLUMN)
            throw new TileUnreachableException("Tile selected is unreachable (out of bound)");

        return tiles[r][c];
    }
}
