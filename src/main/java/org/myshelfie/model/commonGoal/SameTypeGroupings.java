package org.myshelfie.model.commonGoal;

import org.myshelfie.model.Bookshelf;
import org.myshelfie.model.CommonGoalCard;
import org.myshelfie.model.Tile;
import org.myshelfie.model.WrongArgumentException;

/**
 * Common Goal Card: two types of cards are of this type.
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
     * Check if the goal is satisfied.
     * @param bookshelf the bookshelf to check
     * @return true if the goal is satisfied, false otherwise
     * @throws WrongArgumentException when trying to access a tile outside the bookshelf
     */
    @Override
    public Boolean checkGoalSatisfied(Bookshelf bookshelf) throws WrongArgumentException {
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
