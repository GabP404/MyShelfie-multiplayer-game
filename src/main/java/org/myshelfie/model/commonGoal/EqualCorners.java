package org.myshelfie.model.commonGoal;

import org.myshelfie.model.*;

import java.util.ArrayDeque;

public class EqualCorners extends CommonGoalCard {
    /**
     * Initialize the CommonGoalCard associating the points' stack to it
     *
     * @param id
     * @param tokens The token stack that will be placed on the card
     *               NOTE: the stack's generation logic will be in the controller
     */
    public EqualCorners(String id, ArrayDeque<ScoringToken> tokens) {
        super(id, tokens);
    }

    @Override
    public Boolean checkGoalSatisfied(Bookshelf bookshelf) {
        ItemType typesupp;
        try {
            //check top left corner != null
            typesupp = bookshelf.getTile(0,0).getItemType();
            if( typesupp == null)
                return false;

            //check if all other corners are of the same ItemType as the top left corner
            if(bookshelf.getTile(Bookshelf.NUMROWS-1,0).getItemType() == typesupp &&
                    bookshelf.getTile(Bookshelf.NUMROWS-1,Bookshelf.NUMCOLUMNS-1).getItemType() == typesupp &&
                    bookshelf.getTile(0,Bookshelf.NUMCOLUMNS-1).getItemType() == typesupp) {
                return true;
            }

        } catch (TileUnreachableException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
