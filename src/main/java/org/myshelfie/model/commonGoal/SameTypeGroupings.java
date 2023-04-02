package org.myshelfie.model.commonGoal;

import org.myshelfie.model.*;

import java.util.ArrayDeque;

/**
 *  - card 1: Six groups each containing at least 2 tiles of the same type. The tiles of one group can be different from those of another group.
 *  - card 2: Four groups each containing at least 4 tiles of the same type.The tiles of one group can be different from those of another group.
 * This class represents an abstraction of cards 1 and 3, since it generalizes all the possible constraint of the type:
 * bookshelf must have x standalone groups of adjacent tiles with same type (different groups may have different ItemType)
 * each one with dimension greater or equal to y.
 * So the initialization of these two cards must happen as follows:
 * - card 1: SameTypeGroupings('1', 6, 2)
 * - card 2: SameTypeGroupings('2', 4, 4)
 */
public class SameTypeGroupings extends CommonGoalCard {
    private final Integer numGroups;
    private final Integer groupDim;

    public SameTypeGroupings(String id, Integer numGroups, Integer groupDim) {
        super(id);
        this.numGroups = numGroups;
        this.groupDim = groupDim;
    }

    /**
     * This CommonGoalCard checks for the presence of numGroups standalone groups of at least groupDim adjacent tiles
     * of the same type. This method exploits Bookshelf getGroupSize() recursive method to determine groups' size.
     * @param bookshelf The Bookshelf to be analyzed
     * @return Boolean TRUE whether the constraint is satisfied, FALSE if not
     */
    @Override
    public Boolean checkGoalSatisfied(Bookshelf bookshelf) {
        boolean[][] visited = new boolean[Bookshelf.NUMROWS][Bookshelf.NUMCOLUMNS];
        int numGroupsFound = 0;
        for (int i = 0; i < Bookshelf.NUMROWS; i++) {
            for (int j = 0; j < Bookshelf.NUMCOLUMNS; j++) {
                if (!visited[i][j]) {
                    int tmpGroupSize = 0;
                    Tile tmp = bookshelf.getTile(i,j);
                    if (tmp != null) {
                        tmpGroupSize = bookshelf.getGroupSize(visited, i, j, tmp.getItemType());
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
}
