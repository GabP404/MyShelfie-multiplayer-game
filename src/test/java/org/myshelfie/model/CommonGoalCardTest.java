package org.myshelfie.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.myshelfie.model.commonGoal.*;
import org.myshelfie.model.Bookshelf;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CommonGoalCardTest {

    /*
        BOOKSHELFS
     */
    Bookshelf bksEmpty;
    Bookshelf bks1;
    Bookshelf bksCats;
    Bookshelf bksCorners;
    Bookshelf bksCross;
    Bookshelf bksDiagonal;
    Bookshelf bksSquare;
    Bookshelf bksStair;

    /*
        COMMON GOAL CARDS
     */
    CommonGoalCard equalEightCard;
    CommonGoalCard equalCornersCard;
    CommonGoalCard crossTilesCard;
    CommonGoalCard diagonalTilesCard;
    CommonGoalCard squareTilesCard;
    CommonGoalCard stairTilesCard;

    void fillBookshelf(Bookshelf b, int[][] mat) {
        for (int r = Bookshelf.NUMROWS - 1; r >= 0; r--) {
            for (int c = 0; c < Bookshelf.NUMCOLUMNS; c++) {
                try {
                    if (mat[r][c] != -1) {
                        b.insertTile(new Tile(ItemType.values()[mat[r][c]]), c);
                    }
                } catch (TileInsertionException e) {
                    // do nothing
                }
            }
        }
    }

    @BeforeEach
    void setUp() {
        equalEightCard = new EqualEight("EqualEightCard", null);
        equalCornersCard = new EqualCorners("EqualCornersCard", null);
        crossTilesCard = new CrossTiles("CrossTilesCard", null);
        diagonalTilesCard = new DiagonalTiles("DiagonalTilesCard", null);
        squareTilesCard = new SquareTiles("SquareTilesCard", null);
        stairTilesCard = new StairTiles("StairTilesCard", null);


        int[][] a1 = {
                {5, 5, 0, -1, -1},
                {5, 5, 5, 2, 2},
                {1, 1, 5, 5, 0},
                {1, 4, 2, 2, 0},
                {4, 0, 1, 5, 2},
                {3, 1, 1, 2, 2}
        };

        int[][] cats = {
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };

        int[][] corners = {
                {3, 5, 0, -1, 3},
                {5, 5, 5, 2, 2},
                {1, 1, 5, 5, 0},
                {1, 4, 2, 2, 0},
                {4, 0, 1, 5, 2},
                {3, 1, 1, 2, 3}
        };

        int[][] cross = {
                {3, 5, 0, -1, 3},
                {5, 5, 5, 2, 2},
                {4, 1, 4, 5, 0},
                {1, 4, 2, 2, 0},
                {4, 0, 4, 5, 2},
                {3, 1, 1, 2, 3}
        };

        int[][] diagonal = {
                {3, 5, 0, -1, 3},
                {5, 3, 5, 2, 2},
                {4, 1, 3, 5, 0},
                {1, 4, 2, 3, 0},
                {4, 0, 4, 5, 3},
                {0, 1, 1, 2, 3}
        };

        int[][] square = {
                {1, 2, 0, -1, 3},
                {2, 2, 5, 2, 2},
                {4, 1, 3, 2, 2},
                {1, 4, 5, 5, 0},
                {4, 0, 5, 5, 3},
                {0, 1, 1, 2, 3}
        };

        int[][] stair = {
                {-1, -1, -1, -1, -1},
                {-1, -1, -1, -1,  2},
                {-1, -1, -1,  2,  2},
                {-1, -1,  5,  5,  0},
                {-1,  0,  5,  5,  3},
                {0,   1,  1,  2,  3}
        };

        bks1 = new Bookshelf();
        fillBookshelf(bks1, a1);

        bksCats = new Bookshelf();
        fillBookshelf(bksCats, cats);

        bksCorners = new Bookshelf();
        fillBookshelf(bksCorners, corners);

        bksCross = new Bookshelf();
        fillBookshelf(bksCross, cross);

        bksDiagonal = new Bookshelf();
        fillBookshelf(bksDiagonal, diagonal);

        bksSquare = new Bookshelf();
        fillBookshelf(bksSquare, square);

        bksStair = new Bookshelf();
        fillBookshelf(bksStair, stair);

        bksEmpty = new Bookshelf();

    }

    @Test
    void checkGoalSatisfied() {
        EqualEightTest();
        EqualCornersTest();
        CrossTilesTest();
        DiagonalTilesTest();
        SquareTilesTest();
        StairTilesTest();
    }

    @Test
    void EqualEightTest() {
        assertEquals(Boolean.TRUE, equalEightCard.checkGoalSatisfied(bks1));
        assertEquals(Boolean.TRUE, equalEightCard.checkGoalSatisfied(bksCats));
        assertEquals(Boolean.FALSE, equalEightCard.checkGoalSatisfied(bksCorners));
        assertEquals(Boolean.FALSE, equalEightCard.checkGoalSatisfied(bksCross));
        assertEquals(Boolean.FALSE, equalEightCard.checkGoalSatisfied(bksDiagonal));
        assertEquals(Boolean.TRUE, equalEightCard.checkGoalSatisfied(bksSquare));
        assertEquals(Boolean.FALSE, equalEightCard.checkGoalSatisfied(bksStair));
        assertEquals(Boolean.FALSE, equalEightCard.checkGoalSatisfied(bksEmpty));
    }

    @Test
    void EqualCornersTest() {
        assertEquals(Boolean.FALSE, equalCornersCard.checkGoalSatisfied(bks1));
        assertEquals(Boolean.TRUE, equalCornersCard.checkGoalSatisfied(bksCats));
        assertEquals(Boolean.TRUE, equalCornersCard.checkGoalSatisfied(bksCorners));
        assertEquals(Boolean.TRUE, equalCornersCard.checkGoalSatisfied(bksCross));
        assertEquals(Boolean.FALSE, equalCornersCard.checkGoalSatisfied(bksDiagonal));
        assertEquals(Boolean.FALSE, equalCornersCard.checkGoalSatisfied(bksSquare));
        assertEquals(Boolean.FALSE, equalCornersCard.checkGoalSatisfied(bksStair));
        assertEquals(Boolean.FALSE, equalCornersCard.checkGoalSatisfied(bksEmpty));
    }

    @Test
    void CrossTilesTest() {
        assertEquals(Boolean.FALSE, crossTilesCard.checkGoalSatisfied(bks1));
        assertEquals(Boolean.TRUE, crossTilesCard.checkGoalSatisfied(bksCats));
        assertEquals(Boolean.FALSE, crossTilesCard.checkGoalSatisfied(bksCorners));
        assertEquals(Boolean.TRUE, crossTilesCard.checkGoalSatisfied(bksCross));
        assertEquals(Boolean.FALSE, crossTilesCard.checkGoalSatisfied(bksDiagonal));
        assertEquals(Boolean.FALSE, crossTilesCard.checkGoalSatisfied(bksSquare));
        assertEquals(Boolean.FALSE, crossTilesCard.checkGoalSatisfied(bksStair));
        assertEquals(Boolean.FALSE, crossTilesCard.checkGoalSatisfied(bksEmpty));
    }

    @Test
    void DiagonalTilesTest() {
        assertEquals(Boolean.FALSE, diagonalTilesCard.checkGoalSatisfied(bks1));
        assertEquals(Boolean.TRUE, diagonalTilesCard.checkGoalSatisfied(bksCats));
        assertEquals(Boolean.FALSE, diagonalTilesCard.checkGoalSatisfied(bksCorners));
        assertEquals(Boolean.FALSE, diagonalTilesCard.checkGoalSatisfied(bksCross));
        assertEquals(Boolean.TRUE, diagonalTilesCard.checkGoalSatisfied(bksDiagonal));
        assertEquals(Boolean.FALSE, diagonalTilesCard.checkGoalSatisfied(bksSquare));
        assertEquals(Boolean.FALSE, diagonalTilesCard.checkGoalSatisfied(bksStair));
        assertEquals(Boolean.FALSE, diagonalTilesCard.checkGoalSatisfied(bksEmpty));
    }

    @Test
    void SquareTilesTest() {
        assertEquals(Boolean.FALSE, squareTilesCard.checkGoalSatisfied(bks1));
        assertEquals(Boolean.FALSE, squareTilesCard.checkGoalSatisfied(bksCats));
        assertEquals(Boolean.FALSE, squareTilesCard.checkGoalSatisfied(bksCorners));
        assertEquals(Boolean.FALSE, squareTilesCard.checkGoalSatisfied(bksCross));
        assertEquals(Boolean.FALSE, squareTilesCard.checkGoalSatisfied(bksDiagonal));
        assertEquals(Boolean.TRUE, squareTilesCard.checkGoalSatisfied(bksSquare));
        assertEquals(Boolean.FALSE, squareTilesCard.checkGoalSatisfied(bksStair));
        assertEquals(Boolean.FALSE, squareTilesCard.checkGoalSatisfied(bksEmpty));
    }

    @Test
    void StairTilesTest() {
        assertEquals(Boolean.FALSE, stairTilesCard.checkGoalSatisfied(bks1));
        assertEquals(Boolean.FALSE, stairTilesCard.checkGoalSatisfied(bksCats));
        assertEquals(Boolean.FALSE, stairTilesCard.checkGoalSatisfied(bksCorners));
        assertEquals(Boolean.FALSE, stairTilesCard.checkGoalSatisfied(bksCross));
        assertEquals(Boolean.FALSE, stairTilesCard.checkGoalSatisfied(bksDiagonal));
        assertEquals(Boolean.FALSE, stairTilesCard.checkGoalSatisfied(bksSquare));
        assertEquals(Boolean.TRUE, stairTilesCard.checkGoalSatisfied(bksStair));
        assertEquals(Boolean.FALSE, stairTilesCard.checkGoalSatisfied(bksEmpty));
    }



    /*
        DOES NOT WORK.

        @DisplayName("Should analyze the cards correctly")
        @ParameterizedTest(name = "{index} => res={Boolean.TRUE}, b={bks1}")
        @MethodSource("bookShelfProvider")
        void MyEqualEightTest(boolean res, Bookshelf b) {
            assertEquals(res, equalEightCard.checkGoalSatisfied(b));
        }

        private static Stream<Arguments> bookShelfProvider() {
            return Stream.of(
                    Arguments.of(Boolean.TRUE, bks1),
                    Arguments.of(Boolean.TRUE, bksCats),
                    Arguments.of(Boolean.FALSE, bksEmpty)
            );
        }
     */


}