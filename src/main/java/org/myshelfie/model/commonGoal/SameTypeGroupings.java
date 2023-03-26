package org.myshelfie.model.commonGoal;

import org.myshelfie.model.*;

import java.util.ArrayDeque;

/**
 * This class represents an abstraction of cards 1 and 3, since it generalizes all the possible constraint of the type:
 * bookshelf must have x standalone groups of adjacent tiles with same type (different groups may have different ItemType)
 * each one with dimension greater or equal to y.
 * The two cards above may be represented as:
 *  - card 1: 6 groups as described above with size greater or equal to 2
 *  - card 3: 4 groups as descrived above with size greater or equal to 4
 *  Thus the inizalization of these two cards must happen as follows:
 *   card 1: SameTypeGroupings('1', tokens, 6, 2)
 *   card 3: SametypeGroupings('3', tokens, 4, 4)
 */
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
