package org.myshelfie.model.commonGoal;

import org.myshelfie.model.*;

import java.util.ArrayDeque;

public class SameTypeGroupings extends CommonGoalCard {
    private final Integer numGroups;
    private final Integer groupDim;
    /**
     * Initialize the CommonGoalCard associating the points' stack to it
     * @param id The identifier of the card
     * @param tokens The token stack that will be placed on the card
     *               NOTE: the stack's generation logic will be in the controller
     * @param numGroups Number of groups required by card
     * @param groupDim Minimum number of tiles of same type required to form a group
     */
    public SameTypeGroupings(String id, ArrayDeque<ScoringToken> tokens, Integer numGroups, Integer groupDim) {
        super(id, tokens);
        this.numGroups = numGroups;
        this.groupDim = groupDim;
    }
    @Override
    public Boolean checkGoalSatisfied(Bookshelf bookshelf) {
        boolean[][] visited = new boolean[Bookshelf.NUMROWS][Bookshelf.NUMCOLUMNS];
        int numGroupsFound = 0;
        for (int i = 0; i < Bookshelf.NUMROWS; i++) {
            for (int j = 0; j < Bookshelf.NUMCOLUMNS; j++) {
                if (!visited[i][j]) {
                    int tmpGroupSize = 0;
                    Tile tmp;
                    try {
                        tmp = bookshelf.getTile(i,j);
                        try {
                            if (tmp != null) {
                                tmpGroupSize = getGroupSize(visited, i, j, tmp.getItemType(), bookshelf);
                            }
                        } catch (TileUnreachableException e) {
                            // unhandeled exception
                        }
                    } catch (TileUnreachableException e) {
                        // unhandeled exception
                    }
                    numGroupsFound += tmpGroupSize>=groupDim ? 1 : 0;
                    if (numGroupsFound >= numGroups) {
                        return Boolean.TRUE;
                    }
                }
            }
        }
        return Boolean.FALSE;
    }
    private int getGroupSize(boolean[][] visited, int row, int col, ItemType targetType, Bookshelf b) throws TileUnreachableException{
        if (row < 0 || row >= Bookshelf.NUMROWS || col < 0 || col >= Bookshelf.NUMCOLUMNS || visited[row][col] || b.getTile(row,col)==null || b.getTile(row, col).getItemType()!=targetType) {
            return 0;
        }
        visited[row][col] = true;
        int size = 1;
        size += getGroupSize(visited, row - 1, col, targetType, b); // check above
        size += getGroupSize(visited, row + 1, col, targetType, b); // check below
        size += getGroupSize(visited, row, col - 1, targetType, b); // check left
        size += getGroupSize(visited, row, col + 1, targetType, b); // check right
        return size;
    }
}
