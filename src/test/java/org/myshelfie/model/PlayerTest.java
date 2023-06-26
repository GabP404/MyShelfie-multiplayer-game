package org.myshelfie.model;

import org.junit.jupiter.api.Test;
import org.myshelfie.model.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class PlayerTest {

    @Test
    public void testConstructorAndGetterPlayer() {
        Pair<Pair<Integer,Integer>,Tile> p2 = new Pair<>(new Pair<>(0,0),new Tile(ItemType.BOOK));
        List<Pair<Pair<Integer,Integer>,Tile>> lp = new ArrayList<>();
        lp.add(p2);
        PersonalGoalCard pg = new PersonalGoalCard(lp, 0);
        String nick = "User101";
        Player p = new Player(nick,pg);
        assertNotNull(p);
        assertNotNull(p.getBookshelf());
        assertFalse(p.getHasFinalToken());
        assertNotNull(p.getCommonGoalTokens());
        assertNotNull(p.getNickname());
        assertNotNull(p.getTilesPicked());
        assertNotNull(p.getPersonalGoal());
        p.addScoringToken(new ScoringToken(8,"1"));
        p.addScoringToken(new ScoringToken(4,"2"));
        assertEquals(12, p.getPublicPoints());
    }


    @Test
    public void testAddTilesPickedAndRemovedTilesPicked()  {
        Pair<Pair<Integer,Integer>,Tile> p2 = new Pair<>(new Pair<>(0,0),new Tile(ItemType.BOOK));
        List<Pair<Pair<Integer,Integer>,Tile>> lp = new ArrayList<>();
        lp.add(p2);
        PersonalGoalCard pg = new PersonalGoalCard(lp, 0);
        String nick = "User101";
        Player p = new Player(nick,pg);
        assertTrue(p.getTilesPicked().isEmpty());
        LocatedTile t1 = new LocatedTile(ItemType.BOOK, 0, 0);
        LocatedTile t2 = new LocatedTile(ItemType.BOOK, 0, 1);

        try {
            p.addTilesPicked(t1);
            p.addTilesPicked(t2);
        } catch (WrongArgumentException e) {
            fail("Exception thrown" + e.getMessage());
        }
        assertTrue(p.getTilesPicked().contains(t1));

        try {
            p.removeTilesPicked(t1);
        } catch (WrongArgumentException e) {
            fail("Exception thrown" + e.getMessage());
        }
        assertFalse(p.getTilesPicked().contains(t1));
        assertFalse(p.getTilesPicked().isEmpty());
        p.clearHand();
        assertTrue(p.getTilesPicked().isEmpty());
    }

    @Test
    public void testSetterPlayer() {
        Pair<Pair<Integer,Integer>,Tile> p2 = new Pair<>(new Pair<>(0,0),new Tile(ItemType.BOOK));
        List<Pair<Pair<Integer,Integer>,Tile>> lp = new ArrayList<>();
        lp.add(p2);
        PersonalGoalCard pg = new PersonalGoalCard(lp, 0);
        String nick = "User101";
        Player p = new Player(nick,pg);
        assertFalse(p.getHasFinalToken());
        p.setHasFinalToken(true);
        assertTrue(p.getHasFinalToken());
        List<LocatedTile> t = new ArrayList<>();
        LocatedTile t1 = new LocatedTile(ItemType.BOOK, 0, 0, 0);
        LocatedTile t2 = new LocatedTile(ItemType.CAT, 0, 0, 0);
        t.add(t1);
        t.add(t2);
        p.setTilesPicked(t);
        assertEquals(p.getTilesPicked().get(0), t1);
        assertEquals(p.getTilesPicked().get(1), t2);
        p.getTilesPicked().remove(t1);
        assertNotEquals(p.getTilesPicked().get(0), t1);
        assertEquals(p.getTilesPicked().get(0), t2);
    }



}