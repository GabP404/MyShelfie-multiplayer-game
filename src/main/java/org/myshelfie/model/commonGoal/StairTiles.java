package org.myshelfie.model.commonGoal;

import org.myshelfie.model.*;

import java.util.ArrayDeque;

public class StairTiles extends CommonGoalCard {
    /**
     * Initialize the CommonGoalCard associating the points' stack to it
     *
     * @param id
     * @param tokens The token stack that will be placed on the card
     *               NOTE: the stack's generation logic will be in the controller
     */
    public StairTiles(String id, ArrayDeque<ScoringToken> tokens) {
        super(id, tokens);
    }

    @Override
    public Boolean checkGoalSatisfied(Bookshelf bookshelf) {
        /*
            c -> column
            r -> row
            flg -> flag to keep track if the condition is met
        * */
        int c = 0;
        int r = 0;
        boolean flg = true;

        //checking the descending stair
        //finding the higher tile in the first column
        while (true) {
            try {
                if (!(bookshelf.getTile(r, c) == null && r < Bookshelf.NUMROWS)) break;
            } catch (TileUnreachableException outOfBoundTile) {
                // TODO: maybe handle exception
            }
            r++;
        }
        //check if there are enough tile to have a stair
        if (Bookshelf.NUMROWS - r > 5) {
            //analysing tile positions
            while (c < Bookshelf.NUMCOLUMNS - 1 && flg) {
                try {
                    if (bookshelf.getTile(r, c + 1) != null || bookshelf.getTile(r + 1, c + 1) == null) {
                        flg = false;
                    }
                } catch (TileUnreachableException outOfBoundTile) {
                    // TODO: maybe handle exception
                }

                c++;
            }

            if (flg)
                return true;
        }

        //checking the ascending stair
        //finding the higher tile in the first column
        r = 0;
        c = Bookshelf.NUMCOLUMNS - 1;
        flg = true;
        while (true) {
            try {
                if (!(bookshelf.getTile(r, c) == null && r < Bookshelf.NUMROWS)) break;
            } catch (TileUnreachableException outOfBoundTile) {
                // TODO: maybe handle exception
            }
            r++;
        }
        //check if there are enough tile to have a stair
        if (Bookshelf.NUMROWS - r > 5) {
            //analysing tile positions
            while (c > 0 && flg) {
                try {
                    if (bookshelf.getTile(r, c - 1) != null || bookshelf.getTile(r + 1, c - 1) == null) {
                        flg = false;
                    }
                } catch (TileUnreachableException outOfBoundTile) {
                    // TODO: maybe handle exception
                }
                c--;
            }
            if (flg)
                return true;
        }

        return false;


    }

}