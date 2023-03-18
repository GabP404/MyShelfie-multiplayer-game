package org.myshelfie.model.commonGoal;

import org.junit.jupiter.api.*;
import org.myshelfie.model.*;

import static org.junit.jupiter.api.Assertions.*;

class SameTypeGroupingsTest {
    @Test
    void testApp() {
        int[][] a1 = {
                {5, 5, 0, -1, -1},
                {5, 5, 5, 2, 2},
                {1, 1, 5, 5, 0},
                {1, 4, 2, 2, 0},
                {4, 3, 1, 0, 2},
                {3, 1, 1, 2, 2}
        };

        Bookshelf b1 = new Bookshelf();
        for (int r = 0; r < Bookshelf.NUMROWS; r++) {
            for (int c = 0; c < Bookshelf.NUMCOLUMNS; c++) {
                try {
                    if (a1[Bookshelf.NUMROWS - 1 - r][c] != -1) {
                        b1.insertTile(new Tile(ItemType.values()[ a1[Bookshelf.NUMROWS - 1 - r][c]] ), c);
                    }
                } catch (TileInsertionException e){
                    // do nothing
                }
            }
        }
        CommonGoalCard card1 = new SameTypeGroupings(new String("card1"), null, 5, 6);  // requires 5 groups of at least 3 cards of same type -> TRUE
        CommonGoalCard card2 = new SameTypeGroupings(new String("card2"), null, 4, 3);  // requires 4 groups of at least 3 cards of same type -> TRUE
        CommonGoalCard card3 = new SameTypeGroupings(new String("card3"), null, 1, 10);  // requires one group of at least 7 cards of same type -> FALSE

        assertEquals(Boolean.FALSE, card1.checkGoalSatisfied(b1));
        assertEquals(Boolean.TRUE, card2.checkGoalSatisfied(b1));
        assertEquals(Boolean.FALSE, card3.checkGoalSatisfied(b1));
    }
}