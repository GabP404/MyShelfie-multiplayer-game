package org.myshelfie.model.commonGoal;

import org.myshelfie.model.Bookshelf;
import org.myshelfie.model.CommonGoalCard;
import org.myshelfie.model.Tile;
import org.myshelfie.model.WrongArgumentException;

/**
 * Common Goal Card: five tiles of the same type forming a diagonal.
 */
public class DiagonalTiles extends CommonGoalCard {

    public DiagonalTiles(String id) {
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
        /*
            checking descending diagonals
         */
        for (int r = 0; r < Bookshelf.NUMROWS - 4; r++) {
            for (int c = 0; c < Bookshelf.NUMCOLUMNS - 4; c++) {
                if (Boolean.TRUE.equals(checkDiagonal(bookshelf, r, c, 1)))
                    return Boolean.TRUE;
            }
        }
        /*
            checking ascending diagonals
         */
        for (int r = 0; r < Bookshelf.NUMROWS - 4; r++) {
            for (int c = Bookshelf.NUMCOLUMNS - 1; c >= 4; c--) {
                if (Boolean.TRUE.equals(checkDiagonal(bookshelf, r, c, -1)))
                    return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Checks the presence of a single diagonal of five tiles of the same type
     * @param b the bookshelf to check
     * @param r the row of the first tile
     * @param c the column of the first tile
     * @param inclination the inclination of the diagonal
     * @return true if the diagonal is present, false otherwise
     * @throws WrongArgumentException when trying to access a tile outside the bookshelf
     */
    private Boolean checkDiagonal(Bookshelf b, int r, int c, int inclination) throws WrongArgumentException{

        Tile tileSupp;
        Tile tileCurrent;
        /*
            ascending or descending inclination

            1 = DESCENDING
            -1 = ASCENDING
         */
        if (inclination > 0)
            inclination = 1;
        else
            inclination = -1;

        tileSupp = b.getTile(r, c);
        if (tileSupp != null) {
            //analyse the diagonal
            for (int i = 0; i < 5; i++) {
                tileCurrent = b.getTile(r + i, c + (i * inclination));
                if (tileCurrent == null) {
                    return Boolean.FALSE;
                } else {
                    if (tileSupp.getItemType() != tileCurrent.getItemType())
                        return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

}