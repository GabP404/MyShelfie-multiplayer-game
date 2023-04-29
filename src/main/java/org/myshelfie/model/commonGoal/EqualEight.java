package org.myshelfie.model.commonGoal;

import org.myshelfie.model.Bookshelf;
import org.myshelfie.model.CommonGoalCard;
import org.myshelfie.model.ItemType;
import org.myshelfie.model.WrongArgumentException;

/**
 * Eight tiles of the same type. There’s no restriction about the position of these tiles.
 */

public class EqualEight extends CommonGoalCard {

    public EqualEight(String id) {
        super(id);
    }

    @Override
    public Boolean checkGoalSatisfied(Bookshelf bookshelf) throws WrongArgumentException{

        //array to count occurrences of each different type
        int[] enumCount = new int[ItemType.values().length];

        for (int i = 0; i < Bookshelf.NUMROWS; i++) {
            for (int j = 0; j < Bookshelf.NUMCOLUMNS; j++) {
                if (bookshelf.getTile(i, j) != null) {
                    //increment the counter
                    enumCount[bookshelf.getTile(i, j).getItemType().ordinal()]++;
                }
            }
        }

        //analyse the counter
        for (int count : enumCount) {
            if (count >= 8) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}