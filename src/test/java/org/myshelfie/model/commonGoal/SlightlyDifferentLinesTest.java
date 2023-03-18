package org.myshelfie.model.commonGoal;

import org.junit.jupiter.api.Test;
import org.myshelfie.model.*;

import static org.junit.jupiter.api.Assertions.*;

class SlightlyDifferentLinesTest {

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
        CommonGoalCard card1 = new SlightlyDifferentLines(new String("card1"), null, false, 3, 2, 3);
        CommonGoalCard card2 = new SlightlyDifferentLines(new String("card2"), null, false, 3, 2, 4);
        CommonGoalCard card3 = new SlightlyDifferentLines(new String("card3"), null, true, 6, 4, 3);

        assertEquals(Boolean.TRUE, card1.checkGoalSatisfied(b1));
        assertEquals(Boolean.FALSE, card2.checkGoalSatisfied(b1));
        assertEquals(Boolean.TRUE, card3.checkGoalSatisfied(b1));
    }
}