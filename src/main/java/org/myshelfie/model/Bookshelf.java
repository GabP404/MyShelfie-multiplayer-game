package org.myshelfie.model;

import java.util.ArrayList;
import java.util.List;

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

    /**
     *
     * @param r The row of the bookshelf
     * @param c The column of the bookshelf
     * @return The tile in (r, c)
     * @throws TileUnreachableException If (r, c) is out of bounds
     */
    public Tile getTile(int r, int c) throws TileUnreachableException {
        if(r < 0 || r >= NUMROWS || c < 0 || c >= NUMCOLUMNS)
            throw new TileUnreachableException("Tile selected is unreachable (out of bound)");

        return tiles[r][c];
    }

    public int getHeight(int c){
        int r = 0;
        while (r < Bookshelf.NUMROWS && tiles[r][c] == null) {
            r++;
        }
        return NUMROWS - r;
    }

    public List<Integer> getAdjacentSizes() {
        List<Integer> groupSizes = new ArrayList<>();

        boolean[][] visited = new boolean[tiles.length][tiles[0].length];

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (tiles[i][j] == null)
                    visited[i][j] = true;
            }
        }

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (!visited[i][j]) {
                    int groupSize = getGroupSize(visited, i, j, tiles[i][j].getItemType());
                    if (groupSize > 0) {
                        groupSizes.add(groupSize);
                    }
                }
            }
        }

        return groupSizes;
    }

    private int getGroupSize(boolean[][] visited, int row, int col, ItemType value) {
        if (row < 0 || row >= tiles.length || col < 0 || col >= tiles[0].length || visited[row][col] || !value.equals(tiles[row][col].getItemType())) {
            return 0;
        }

        visited[row][col] = true;

        int size = 1;
        size += getGroupSize(visited, row - 1, col, value); // check above
        size += getGroupSize(visited, row + 1, col, value); // check below
        size += getGroupSize(visited, row, col - 1, value); // check left
        size += getGroupSize(visited, row, col + 1, value); // check right

        return size;
    }
}
