package org.myshelfie.model.commonGoal;

import org.myshelfie.model.*;

import java.util.ArrayDeque;

/*
    Five tiles of the same type forming an X.
 */

public class CrossTiles extends CommonGoalCard {
    /**
     * Initialize the CommonGoalCard associating the points' stack to it
     *
     * @param id String that identifies the card
     * @param tokens The token stack that will be placed on the card
     *               NOTE: the stack's generation logic will be in the controller
     */
    public CrossTiles(String id, ArrayDeque<ScoringToken> tokens) {
        super(id, tokens);
    }

    @Override
    public Boolean checkGoalSatisfied(Bookshelf bookshelf) {

        Tile tileSupp;
        //ItemType typesupp;

        for (int r = 0; r < Bookshelf.NUMROWS - 2; r++) {
            for (int c = 0; c < Bookshelf.NUMCOLUMNS - 2; c++) {
                //typesupp = bookshelf.getTile(r, c).getItemType();
                tileSupp = bookshelf.getTile(r, c);
                if (tileSupp != null) {
                    if (bookshelf.getTile(r, c + 2) != null &&
                            bookshelf.getTile(r + 1, c + 1) != null &&
                            bookshelf.getTile(r + 2, c) != null &&
                            bookshelf.getTile(r + 2, c + 2) != null) {
                        if (
                                bookshelf.getTile(r, c + 2).getItemType() == tileSupp.getItemType() &&
                                        bookshelf.getTile(r + 1, c + 1).getItemType() == tileSupp.getItemType() &&
                                        bookshelf.getTile(r + 2, c).getItemType() == tileSupp.getItemType() &&
                                        bookshelf.getTile(r + 2, c + 2).getItemType() == tileSupp.getItemType()
                        ) {
                            return Boolean.TRUE;
                        }
                    }
                }

            }
        }
        return Boolean.FALSE;
    }

}