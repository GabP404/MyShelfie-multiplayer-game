package org.myshelfie.model;

public class Bookshelf {

    public static final int NUMROWS = 6;
    public static final int NUMCOLUMNS = 5;

    private Tile[][] tiles;


    /**
     * Bookshelf constructor.
     * It initializes an empty bookshelf.
     */
    public Bookshelf() {
        tiles = new Tile[NUMROWS][NUMCOLUMNS];
        for (int i = 0; i < NUMROWS; i++) {
           for (int j = 0; j < NUMCOLUMNS; j++) {
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

        int i = NUMROWS -1;
        while (tiles[i][c] != null) {
            i--;
        }
        tiles[i][c] = t;
    }

    //parameters are row(r) and column(c)
    public Tile getTile(int r, int c) throws TileUnreachableException {
        if(r < 0 || r >= NUMROWS || c < 0 || c >= NUMCOLUMNS)
            throw new TileUnreachableException("Tile selected is unreachable (out of bound)");

        return tiles[r][c];
    }
}
