package org.myshelfie.model.commonGoal;

import org.myshelfie.model.Bookshelf;
import org.myshelfie.model.CommonGoalCard;
import org.myshelfie.model.Tile;
import org.myshelfie.model.WrongArgumentException;

/**
 * Five tiles of the same type forming an X.
 */

public class CrossTiles extends CommonGoalCard {

    public CrossTiles(String id) {
        super(id);
    }

    @Override
    public Boolean checkGoalSatisfied(Bookshelf bookshelf) throws WrongArgumentException {

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