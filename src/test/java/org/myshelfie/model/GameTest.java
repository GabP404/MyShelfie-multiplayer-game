package org.myshelfie.model;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    Game g1;


    @Test
    public void testConstructorAndGetter() throws IOException, URISyntaxException, WrongArgumentException {
        int standard_game = 2;
        int numPlayer = 2;
        CommonGoalDeck cgd = CommonGoalDeck.getInstance();
        PersonalGoalDeck pgc = PersonalGoalDeck.getInstance();
        List<Player> players = new ArrayList<>();
        List<PersonalGoalCard> pgcGame = pgc.draw(numPlayer);
        for (int i = 0; i < 2; i++) {
            players.add(new Player(("test"+ i),pgcGame.get(i)));
        }
        List<CommonGoalCard> cgc = cgd.drawCommonGoalCard(standard_game);
        HashMap<CommonGoalCard,List<ScoringToken>> commonGoal = new HashMap<>();
        for (CommonGoalCard x : cgc) {
            commonGoal.put(x, (List<ScoringToken>) createTokensPersonalGoalCard(x.getId(),numPlayer));
        }
        TileBag tb = new TileBag();

        this.g1 = new Game(players,new Board(numPlayer),commonGoal,tb,ModelState.CREATED_GAME);
        assertNotNull(g1);
        assertNotNull(g1.getPlayers());
        assertNotNull(g1.getBoard());
        assertNotNull(g1.getCommonGoals());
        assertNotNull(g1.getTileBag());
        assertNotNull(g1.getCurrPlayer());
        assertFalse(g1.isPlaying());
        this.g1.startGame();
        assertTrue(this.g1.isPlaying());
        this.g1.getTopScoringToken(cgc.get(0));
        ScoringToken st = this.g1.popTopScoringToken(cgc.get(0));
        assertNotEquals(this.g1.getTopScoringToken(cgc.get(0)),st);
        this.g1.setCurrPlayer(this.g1.getNextPlayer());
        assertEquals(this.g1.getCurrPlayer(),this.g1.getPlayers().get(1));
        int pos = this.g1.getPlayers().indexOf(g1.getCurrPlayer());
        assertEquals(this.g1.getNextPlayer(),g1.getPlayers().get(0));
    }

    private LinkedList<ScoringToken> createTokensPersonalGoalCard(String id, int numPlayer) {
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
}