package org.myshelfie.model.commonGoal;

import org.myshelfie.model.*;

import java.util.ArrayDeque;

/*
    Five tiles of the same type forming a diagonal.
 */

public class DiagonalTiles extends CommonGoalCard {
    /**
     * Initialize the CommonGoalCard associating the points' stack to it
     *
     * @param id String that identifies the card
     * @param tokens The token stack that will be placed on the card
     *               NOTE: the stack's generation logic will be in the controller
     */
    public DiagonalTiles(String id, ArrayDeque<ScoringToken> tokens) {
        super(id, tokens);
    }

    @Override
    public Boolean checkGoalSatisfied(Bookshelf bookshelf) {
        /*
            checking descending diagonals
         */
        for (int r = 0; r < Bookshelf.NUMROWS - 4; r++) {
            for (int c = 0; c < Bookshelf.NUMCOLUMNS - 4; c++) {
                if (Boolean.TRUE.equals(checkDiagonal(bookshelf, r, c, 1)))
                    return true;
            }
        }
        /*
            checking ascending diagonals
         */
        for (int r = Bookshelf.NUMROWS - 1; r >= 4; r--) {
            for (int c = Bookshelf.NUMCOLUMNS - 1; c >= 4; c--) {
                if (Boolean.TRUE.equals(checkDiagonal(bookshelf, r, c, -1)))
                    return true;
            }
        }
        return false;
    }

    private Boolean checkDiagonal(Bookshelf b, int r, int c, int inclination) {

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

        tileSupp = b.getTile(r, c);//.getItemType();
        if (tileSupp != null) {
            //analyse the diagonal
            for (int i = 0; i < 5; i++) {
                tileCurrent = b.getTile(r + (i * inclination), c + (i * inclination));
                if (tileCurrent == null) {
                    return false;
                } else {
                    if (tileSupp.getItemType() != tileCurrent.getItemType())
                        return false;
                }
            }
            return true;
        }

        return false;
    }

}