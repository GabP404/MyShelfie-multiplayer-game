package org.myshelfie.model.commonGoal;

import org.myshelfie.model.Bookshelf;
import org.myshelfie.model.CommonGoalCard;
import org.myshelfie.model.WrongArgumentException;

/**
 * Common Goal Card: five columns of increasing or decreasing height. Starting from the first column on
 * the left or on the right, each next column must be made of exactly one more tile. Tiles can be of any type.
 */
public class StairTiles extends CommonGoalCard {

    public StairTiles(String id) {
        super(id);
    }

    /**
     * Check if the goal is satisfied.
     * @param bookshelf the bookshelf to check
     * @return true if the goal is satisfied, false otherwise
     * @throws WrongArgumentException when trying to access a tile outside the bookshelf
     */
    @Override
    public Boolean checkGoalSatisfied(Bookshelf bookshelf) throws WrongArgumentException{
        //checking the stair

        int pre;    //height of the precedent column in the stair
        int cur;    //height of the current column in the stair
        int c = 0;  //column counter
        boolean flgD = true;    //flag descending stair
        boolean flgA = true;    //flag ascending stair

        //save the height of the first column
        pre = bookshelf.getHeight(c);
        c++;
        while (c < Bookshelf.NUMCOLUMNS && (flgA || flgD)) {
            //save the height of the current column
            cur = bookshelf.getHeight(c);
            if (pre != cur + 1)
                flgD = false;

            if (pre != cur - 1)
                flgA = false;

            //the current height becomes the precedent height
            pre = cur;
            c++;
        }
        return flgA || flgD;
    }

}