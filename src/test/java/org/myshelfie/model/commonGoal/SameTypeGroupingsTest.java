package org.myshelfie.model.commonGoal;

import org.junit.jupiter.params.provider.MethodSource;
import org.myshelfie.model.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
class SameTypeGroupingsTest {

    @MethodSource
    void testApp() {
        int[][] a1 = {
                {5,  5,  0, -1, -1},
                {5,  5,  5, -1,  2},
                {1,  1,  1,  5,  2},
                {1,  1,  4,  1,  2},
                {1,  4,  1,  1,  2},
                {3,  1,  1,  2,  2}
        };

        int[][] a2 = {
                {5, -1,  0, -1, -1},
                {5, -1,  5, -1,  2},
                {5,  5,  5,  5,  5},
                {1,  4,  5,  2,  5},
                {3,  3,  2,  2,  5},
                {3,  1,  5,  5,  5}
        };

        int[][] a3 = {
                {-1, -1, -1, -1, -1},
                {-1, -1, -1, -1, -1},
                {-1, -1, -1, -1,  5},
                {-1,  4, -1, -1,  2},
                { 3,  3, -1,  0,  5},
                { 3,  1,  3,  4,  0}
        };

        Bookshelf b1 = new Bookshelf();
        Bookshelf b2 = new Bookshelf();
        Bookshelf b3 = new Bookshelf();

        for (int r = 0; r < Bookshelf.NUMROWS; r++) {
            for (int c = 0; c < Bookshelf.NUMCOLUMNS; c++) {
                try {
                    if (a1[Bookshelf.NUMROWS - 1 - r][c] != -1) {
                        b1.insertTile(new Tile(ItemType.values()[ a1[Bookshelf.NUMROWS - 1 - r][c]] ), c);
                    }
                    if (a2[Bookshelf.NUMROWS - 1 - r][c] != -1) {
                        b2.insertTile(new Tile(ItemType.values()[ a2[Bookshelf.NUMROWS - 1 - r][c]] ), c);
                    }
                    if (a3[Bookshelf.NUMROWS - 1 - r][c] != -1) {
                        b3.insertTile(new Tile(ItemType.values()[ a3[Bookshelf.NUMROWS - 1 - r][c]] ), c);
                    }
                } catch (TileInsertionException e){
                    // do nothing
                }
            }
        }
        CommonGoalCard card1 = new SameTypeGroupings(new String("card1"), null, 4, 5);  // requires 5 groups of at least 3 cards of same type -> TRUE
        CommonGoalCard card2 = new SameTypeGroupings(new String("card2"), null, 2, 3);  // requires 4 groups of at least 3 cards of same type -> TRUE
        CommonGoalCard card3 = new SameTypeGroupings(new String("card3"), null, 1, 10);  // requires one group of at least 7 cards of same type -> FALSE

        assertEquals(Boolean.TRUE, card1.checkGoalSatisfied(b1));
        assertEquals(Boolean.TRUE, card2.checkGoalSatisfied(b1));
        assertEquals(Boolean.FALSE, card3.checkGoalSatisfied(b1));

        assertEquals(Boolean.FALSE, card1.checkGoalSatisfied(b2));
        assertEquals(Boolean.TRUE, card2.checkGoalSatisfied(b2));
        assertEquals(Boolean.TRUE, card3.checkGoalSatisfied(b2));

        assertEquals(Boolean.FALSE, card1.checkGoalSatisfied(b3));
        assertEquals(Boolean.FALSE, card2.checkGoalSatisfied(b3));
        assertEquals(Boolean.FALSE, card3.checkGoalSatisfied(b3));
    }
}