package org.myshelfie.controller;

import org.junit.jupiter.api.Test;
import org.myshelfie.model.PersonalGoalCard;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigurationTest {
    @Test
    public void testCreatePersonalGoalDeck() {
        List<PersonalGoalCard> result = Configuration.createPersonalGoalDeck();
        assertNotNull(result);
    }

    @Test
    public void testGetPersonalGoalPoints() {
        Map<Integer, Integer> result = Configuration.getPersonalGoalPoints();
        assertNotNull(result);
    }

    @Test
    public void testGetCommonGoalCardDescription() {
        String result = Configuration.getCommonGoalCardDescription("1");
        assertNotNull(result);
    }

    @Test
    public void testGetFinalPoints() {
        int result = Configuration.getFinalPoints();
        assertNotNull(result);
    }

    @Test
    public void testGetBoardMask() {
        int[][] result = Configuration.getBoardMask();
        assertNotNull(result);
    }

    @Test
    public void testGetBoardDimension() {
        int result = Configuration.getBoardDimension();
        assertNotNull(result);
    }

    @Test
    public void testGetBookshelfRows() {
        int result = Configuration.getBookshelfRows();
        assertNotNull(result);
    }

    @Test
    public void testGetBookshelfCols() {
        int result = Configuration.getBookshelfCols();
        assertNotNull(result);
    }

    @Test
    public void testGetTilesPerType() {
        int result = Configuration.getTilesPerType();
        assertNotNull(result);
    }

    @Test
    public void testGetTimerTimeout() {
        int result = Configuration.getTimerTimeout();
        assertNotNull(result);
    }

    @Test
    public void testGetMapPointsGroup() {
        Map<Integer, Integer> result = Configuration.getMapPointsGroup();
        assertNotNull(result);
    }

    @Test
    public void testGetServerAddress() {
        String result = Configuration.getServerAddress();
        assertNotNull(result);
    }

    @Test
    public void testGetServerSocketPort() {
        int result = Configuration.getServerSocketPort();
        assertNotNull(result);
    }

    @Test
    public void testGetServerRMIName() {
        String result = Configuration.getServerRMIName();
        assertNotNull(result);
    }

    @Test
    public void testGetServerBackupFileName() {
        String result = Configuration.getServerBackupFileName();
        assertNotNull(result);
    }

    @Test
    public void testGetServerLogFileName() {
        String result = Configuration.getServerLogFileName();
        assertNotNull(result);
    }

    @Test
    public void testGetServerLogLevel() {
        Level result = Configuration.getServerLogLevel();
        assertNotNull(result);
    }
}
