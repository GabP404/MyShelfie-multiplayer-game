package org.myshelfie.model;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    Game g1;
    int standard_game = 2;
    int numPlayer = 2;
    CommonGoalDeck cgd = CommonGoalDeck.getInstance();
    PersonalGoalDeck pgc = PersonalGoalDeck.getInstance();
    List<PersonalGoalCard> pgcGame = pgc.draw(numPlayer);
    List<Player> players = Arrays.asList(
            new Player(("test0"),pgcGame.get(0)),
            new Player(("test1"),pgcGame.get(1))
    );

    List<CommonGoalCard> cgc = cgd.drawCommonGoalCard(standard_game);
    HashMap<CommonGoalCard,List<ScoringToken>> commonGoal = new HashMap<>();
    TileBag tb = new TileBag();

    public GameTest() throws IOException, URISyntaxException {
    }


    @Test
    public void testConstructorAndGetter() throws IOException, URISyntaxException, WrongArgumentException {
        for (CommonGoalCard x : cgc) {
            commonGoal.put(x, (List<ScoringToken>) createTokensPersonalGoalCard(x.getId(),numPlayer));
        }
        this.g1 = new Game();
        this.g1.setupGame(players, new Board(numPlayer),commonGoal,tb,ModelState.WAITING_SELECTION_TILE, "testGame");
        assertNotNull(g1);
        assertNotNull(g1.getPlayers());
        assertNotNull(g1.getBoard());
        assertNotNull(g1.getCommonGoals());
        assertNotNull(g1.getTileBag());
        assertNotNull(g1.getCurrPlayer());
        assertEquals(g1.getGameName(), "testGame");
        assertTrue(g1.isPlaying());
        // TODO update this test to take the new state pattern into account
        this.g1.getTopScoringToken(cgc.get(0));
        ScoringToken st = this.g1.popTopScoringToken(cgc.get(0));
        assertNotEquals(this.g1.getTopScoringToken(cgc.get(0)),st);
        this.g1.popTopScoringToken(cgc.get(0));
        assertNull(this.g1.popTopScoringToken(cgc.get(0))); // All tokens taken
        this.g1.setCurrPlayer(this.g1.getNextPlayer());
        assertEquals(this.g1.getCurrPlayer(),this.g1.getPlayers().get(1));
        int pos = this.g1.getPlayers().indexOf(g1.getCurrPlayer());
        assertEquals(this.g1.getNextPlayer(),g1.getPlayers().get(0));
        assertEquals(this.g1.getCommonGoalsMap(), commonGoal);
        this.g1.setErrorState("test0","Error message!");
        assertEquals(this.g1.getErrorState("test0"),"Error message!");
        this.g1.resetErrorState();
        assertNull(this.g1.getErrorState("test0"));
        assertEquals(this.g1.getNumOnlinePlayers(), 2);
    }

    // Utility method to create scoring tokens for personal goal cards
    public static LinkedList<ScoringToken> createTokensPersonalGoalCard(String id, int numPlayer) {
        LinkedList<ScoringToken> tokens = new LinkedList<>();
        switch (numPlayer) {
            case 2 -> {
                tokens.add(new ScoringToken(8, id));
                tokens.add(new ScoringToken(4, id));
            }
            case 3 -> {
                tokens.add(new ScoringToken(8, id));
                tokens.add(new ScoringToken(6, id));
                tokens.add(new ScoringToken(4, id));
            }
            case 4 -> {
                tokens.add(new ScoringToken(8, id));
                tokens.add(new ScoringToken(6, id));
                tokens.add(new ScoringToken(4, id));
                tokens.add(new ScoringToken(2, id));
            }
        }
        return tokens;
    }

    @Test
    public void testGetNextOnlinePlayer() throws WrongArgumentException {
        List<Player> playerCopy = new ArrayList<>(players);
        playerCopy.add(new Player(("test2"),pgcGame.get(0)));
        for (CommonGoalCard x : cgc) {
            commonGoal.put(x, (List<ScoringToken>) createTokensPersonalGoalCard(x.getId(),numPlayer));
        }
        this.g1 = new Game();
        this.g1.setupGame(playerCopy, new Board(numPlayer),commonGoal,tb,ModelState.WAITING_SELECTION_TILE, "testGame");
        playerCopy.get(0).setOnline(false);
        assertEquals(playerCopy.get(1), g1.getNextOnlinePlayer());
        g1.setCurrPlayer(playerCopy.get(1));
        playerCopy.get(2).setOnline(false);
        assertEquals(playerCopy.get(1), g1.getNextOnlinePlayer());
    }

    @Test
    public void testStatesProgression() {
        for (CommonGoalCard x : cgc) {
            commonGoal.put(x, createTokensPersonalGoalCard(x.getId(),numPlayer));
        }
        this.g1 = new Game();
        this.g1.setupGame(players, new Board(numPlayer),commonGoal,tb,ModelState.WAITING_SELECTION_TILE, "testGame");
        assertEquals(ModelState.WAITING_SELECTION_TILE, g1.getModelState());

        g1.setModelState(ModelState.WAITING_SELECTION_BOOKSHELF_COLUMN);
        assertEquals(ModelState.WAITING_SELECTION_BOOKSHELF_COLUMN, g1.getModelState());

        g1.saveState();
        g1.setModelState(ModelState.PAUSE);
        assertEquals(ModelState.PAUSE, g1.getModelState());

        g1.resumeStateAfterPause();
        assertEquals(ModelState.WAITING_SELECTION_BOOKSHELF_COLUMN, g1.getModelState());
    }
}