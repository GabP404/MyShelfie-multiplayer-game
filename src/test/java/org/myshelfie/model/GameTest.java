package org.myshelfie.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.myshelfie.model.commonGoal.CrossTiles;
import org.myshelfie.model.util.Pair;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GameTest {
    Game g1;

    public void testConstructorAndGetter() {
        Pair<Pair<Integer,Integer>,Tile> p1 = new Pair<>(new Pair<>(0,0),new Tile(ItemType.BOOK));
        List<Pair<Pair<Integer,Integer>,Tile>> lp = new ArrayList<>();
        lp.add(p1);
        PersonalGoalCard pg1 = new PersonalGoalCard(lp);
        String nick1 = "User1";
        Player player1 = new Player(nick1,pg1);

        Pair<Pair<Integer,Integer>,Tile> p2 = new Pair<>(new Pair<>(1,1),new Tile(ItemType.CAT));
        List<Pair<Pair<Integer,Integer>,Tile>> lp2 = new ArrayList<>();
        lp2.add(p2);
        PersonalGoalCard pg2 = new PersonalGoalCard(lp2);
        String nick2 = "User2";
        Player player2 = new Player(nick2,pg2);

        List<Player> listPlayers = new ArrayList<>();
        listPlayers.add(player1);
        listPlayers.add(player2);


        ScoringToken st1 = new ScoringToken(4,"CG1");
        ArrayDeque<ScoringToken> scoringTokensDeq1= new ArrayDeque<>();
        scoringTokensDeq1.add(st1);
        CommonGoalCard cg1 = new CrossTiles("CG1",scoringTokensDeq1);

        ScoringToken st3 = new ScoringToken(4,"CG2");
        ArrayDeque<ScoringToken> scoringTokensDeq2 = new ArrayDeque<>();
        scoringTokensDeq2.add(st1);
        CommonGoalCard cg2 = new CrossTiles("CG2",scoringTokensDeq2);

        List<CommonGoalCard> listcg = new ArrayList<>();
        listcg.add(cg1);
        listcg.add(cg2);

        g1 = new Game(listPlayers,new Board(2),listcg,new TileBag());
        assertNotNull(g1);
        assertNotNull(g1.getPlayers());
        assertNotNull(g1.getBoard());
        assertNotNull(g1.getCommonGoals());
        assertNotNull(g1.getTileBag());
        assertNotNull(g1.getCurrPlayer());
        assertFalse(g1.isPlaying());
    }

   @Test
    public void testGetNextPlayer() {
       testConstructorAndGetter();
       int pos = g1.getPlayers().indexOf(g1.getCurrPlayer());
       assertEquals(g1.getNextPlayer(),g1.getPlayers().get(pos+1));
    }

}