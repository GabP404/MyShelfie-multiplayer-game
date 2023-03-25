package org.myshelfie.model.commonGoal;

import org.myshelfie.model.*;

import java.util.ArrayDeque;

/*
    Four tiles of the same type in the four
    corners of the bookshelf.
 */
public class EqualCorners extends CommonGoalCard {
    /**
     * Initialize the CommonGoalCard associating the points' stack to it
     *
     * @param id String that identifies the card
     * @param tokens The token stack that will be placed on the card
     *               NOTE: the stack's generation logic will be in the controller
     */
    public EqualCorners(String id, ArrayDeque<ScoringToken> tokens) {
        super(id, tokens);
    }

    @Override
    public Boolean checkGoalSatisfied(Bookshelf bookshelf) {
        //ItemType typesupp;
        Tile tilesupp;
        //check top left corner != null
        tilesupp = bookshelf.getTile(0, 0);
        if (tilesupp == null)
            return Boolean.FALSE;

        //check if all other corners are of the same ItemType as the top left corner
        if (bookshelf.getTile(Bookshelf.NUMROWS - 1, 0) == null ||
                bookshelf.getTile(Bookshelf.NUMROWS - 1, Bookshelf.NUMCOLUMNS - 1) == null ||
                bookshelf.getTile(0, Bookshelf.NUMCOLUMNS - 1) == null) {
            return Boolean.FALSE;
        }

        return bookshelf.getTile(Bookshelf.NUMROWS - 1, 0).getItemType() == tilesupp.getItemType() &&
                bookshelf.getTile(Bookshelf.NUMROWS - 1, Bookshelf.NUMCOLUMNS - 1).getItemType() == tilesupp.getItemType() &&
                bookshelf.getTile(0, Bookshelf.NUMCOLUMNS - 1).getItemType() == tilesupp.getItemType();
    }
}
