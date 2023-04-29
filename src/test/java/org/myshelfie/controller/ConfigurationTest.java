package org.myshelfie.controller;

import org.junit.jupiter.api.Test;
import org.myshelfie.model.PersonalGoalCard;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationTest {

    @Test
    public void testCreatePersonalGoalDeck() {
        List<PersonalGoalCard> deck = Configuration.createPersonalGoalDeck();
        assertNotNull(deck);
        assertInstanceOf(PersonalGoalCard.class, deck.get(0));
    }

    @Test
    public void testGetPersonalGoalPoints() {
        Map<Integer, Integer> points = Configuration.getPersonalGoalPoints();
        assertNotNull(points);
        assertTrue(points.size() > 0);
    }

    @Test
    public void testGetFinalPoints() {
        int finalPoints = Configuration.getFinalPoints();
        assertTrue(finalPoints > 0);
    }

    @Test
    public void testGetBoardMask() {
        int[][] mask = Configuration.getBoardMask();
        assertNotNull(mask);
        for (int[] ints : mask) {
            for (int anInt : ints) {
                assert anInt > 0;
            }
        }
    }

    @Test
    public void testGetBoardDimension() {
        int dimension = Configuration.getBoardDimension();
        assertTrue(dimension > 0);
    }

    @Test
    public void testGetBookshelfRows() {
        int rows = Configuration.getBookshelfRows();
        assertTrue(rows > 0);
    }

    @Test
    public void testGetBookshelfCols() {
        int cols = Configuration.getBookshelfCols();
        assertTrue(cols > 0);
    }

    @Test
    public void testGetTilesPerType() {
        int tilesPerType = Configuration.getTilesPerType();
        assert tilesPerType == 22;
    }
}