package org.myshelfie.model.commonGoal;

import org.myshelfie.model.*;

import java.util.ArrayDeque;

/*
    Eight tiles of the same type. There’s no
    restriction about the position of these tiles.
 */

public class EqualEight extends CommonGoalCard {
    /**
     * Initialize the CommonGoalCard associating the points' stack to it
     *
     * @param id
     * @param tokens The token stack that will be placed on the card
     *               NOTE: the stack's generation logic will be in the controller
     */
    public EqualEight(String id, ArrayDeque<ScoringToken> tokens) {
        super(id, tokens);
    }

    @Override
    public Boolean checkGoalSatisfied(Bookshelf bookshelf) {

        //array to count occurrences of each different type
        int[] enumCount = new int[ItemType.values().length];

        for (int i = 0; i < Bookshelf.NUMROWS; i++) {
            for (int j = 0; j < Bookshelf.NUMCOLUMNS; j++) {
                try {
                    if (bookshelf.getTile(i, j) != null) {
                        //increment the counter
                        enumCount[bookshelf.getTile(i, j).getItemType().ordinal()]++;
                    }
                } catch (TileUnreachableException outOfBoundTile) {
                    // TODO: maybe handle exception
                }

            }
        }

        //analyse the counter
        for (int count : enumCount) {
            if (count >= 8) {
                return true;
            }
        }
        return false;
    }
}