package org.myshelfie.model.commonGoal;

import org.myshelfie.model.*;

import java.util.ArrayDeque;

/*
    Two groups each containing 4 tiles of
    the same type in a 2x2 square. The tiles
    of one square can be different from
    those of the other square.
 */

public class SquareTiles extends CommonGoalCard {
    /**
     * Initialize the CommonGoalCard associating the points' stack to it
     *
     * @param id String that identifies the card
     * @param tokens The token stack that will be placed on the card
     *               NOTE: the stack's generation logic will be in the controller
     */
    public SquareTiles(String id, ArrayDeque<ScoringToken> tokens) {
        super(id, tokens);
    }

    @Override
    public Boolean checkGoalSatisfied(Bookshelf bookshelf) {

        boolean[][] visited = new boolean[Bookshelf.NUMROWS][Bookshelf.NUMCOLUMNS];
        //number of squares to be found
        int numGroups = 2;
        //counter for the squares
        int numGroupsFound = 0;

        for (int i = 0; i < Bookshelf.NUMROWS - 1; i++) {
            for (int j = 0; j < Bookshelf.NUMCOLUMNS - 1; j++) {
                if (!visited[i][j]) {
                    int tmpGroupSize = 0;
                    Tile tmp = bookshelf.getTile(i, j);
                    if (tmp != null) {
                        tmpGroupSize = bookshelf.getGroupSize(visited, i, j, tmp.getItemType());
                    }
                    //if the group has 4 tiles analyze their shape
                    if (tmpGroupSize == 4) {
                        if (bookshelf.getTile(i, j + 1).getItemType() == tmp.getItemType() &&
                                bookshelf.getTile(i + 1, j).getItemType() == tmp.getItemType() &&
                                bookshelf.getTile(i + 1, j + 1).getItemType() == tmp.getItemType()) {
                            numGroupsFound++;
                        }
                    }
                    if (numGroupsFound >= numGroups) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}