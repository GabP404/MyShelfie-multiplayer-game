package org.myshelfie.model.commonGoal;

import org.myshelfie.model.Bookshelf;
import org.myshelfie.model.CommonGoalCard;
import org.myshelfie.model.Tile;
import org.myshelfie.model.WrongArgumentException;

/**
 * Common Goal Card: four tiles of the same type in the four corners of the bookshelf.
 */
public class EqualCorners extends CommonGoalCard {

    public EqualCorners(String id) {
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
