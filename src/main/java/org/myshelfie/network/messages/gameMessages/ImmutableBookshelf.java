package org.myshelfie.network.messages.gameMessages;

import org.myshelfie.model.Bookshelf;
import org.myshelfie.model.ItemType;
import org.myshelfie.model.Tile;
import org.myshelfie.model.TileUnreachableException;

import java.util.ArrayList;
import java.util.List;

public final class ImmutableBookshelf {
    public final int NUMROWS;
    public final int NUMCOLUMNS;

    private final Tile[][] tiles;

    public ImmutableBookshelf(Bookshelf b) {
        this.NUMROWS = Bookshelf.NUMROWS;
        this.NUMCOLUMNS = Bookshelf.NUMCOLUMNS;
        tiles = new Tile[NUMROWS][NUMCOLUMNS];
        for (int i = 0; i < NUMROWS; i++) {
            for (int j = 0; j < NUMCOLUMNS; j++) {
                tiles[i][j] = b.getTile(i,j);
            }
        }
    }

    /**
     * Retrieves tile in position (r,c)
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


    /**
     * Method that returns the height of a certain column of the bookshelf intended as the number of non-null tiles in it.
     * @param c Column of interest
     * @return The height of the column c inside this Bookshelf
     */
    public int getHeight(int c){
        int r = 0;
        while (r < Bookshelf.NUMROWS && tiles[r][c] == null) {
            r++;
        }
        return NUMROWS - r;
    }

    /**
     * Examines all the bookshelf to produce a list of the sizes of all the standalone groups of adjacent tiles
     * of the same type. This method allows the computing of scoring points related to adjacent tiles.
     * @return The list containing the sizes of all groups
     */
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

    /**
     * Recursive method that allows to compute the size of standalone groups of adjacent tiles of same type.
     * Once checked that the current tile has targetType, size is incremented by one and then incremented by the result
     * of all the recursive calls to all the adjacent tiles.
     * @param visited A boolean matrix with same size as tiles[][] used to avoid considering more that once the same tile
     * @param row The row index
     * @param col The column index
     * @param targetType ItemType of the group's tiles
     * @return The size of the subgroup
     */
    public int getGroupSize(boolean[][] visited, int row, int col, ItemType targetType) {
        if (row < 0 || row >= NUMROWS || col < 0 || col >= NUMCOLUMNS || visited[row][col] || tiles[row][col]==null || targetType!=tiles[row][col].getItemType()) {
            return 0;
        }

        visited[row][col] = true;

        int size = 1;
        size += getGroupSize(visited, row - 1, col, targetType); // check above
        size += getGroupSize(visited, row + 1, col, targetType); // check below
        size += getGroupSize(visited, row, col - 1, targetType); // check left
        size += getGroupSize(visited, row, col + 1, targetType); // check right

        return size;
    }


}
