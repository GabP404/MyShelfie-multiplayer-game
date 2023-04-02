package org.myshelfie.model;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myshelfie.model.util.Pair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;


class PlayerTest {

    @Test
    public void testConstructorAndGetterPlayer() throws IOException {
        Pair<Pair<Integer,Integer>,Tile> p2 = new Pair<>(new Pair<>(0,0),new Tile(ItemType.BOOK));
        List<Pair<Pair<Integer,Integer>,Tile>> lp = new ArrayList<>();
        lp.add(p2);
        PersonalGoalCard pg = new PersonalGoalCard(lp);
        String nick = "User101";
        Player p = new Player(nick,pg);
        assertNotNull(p);
        assertNotNull(p.getBookshelf());
        assertFalse(p.getHasFinalToken());
        assertNotNull(p.getCommonGoalTokens());
        assertNotNull(p.getNickname());
        assertNotNull(p.getTilesPicked());
        assertNotNull(p.getPersonalGoal());
    }

    @Test
    public void testAddTilesPickedAndRemovedTilesPicked() throws TileInsertionException {
        Pair<Pair<Integer,Integer>,Tile> p2 = new Pair<>(new Pair<>(0,0),new Tile(ItemType.BOOK));
        List<Pair<Pair<Integer,Integer>,Tile>> lp = new ArrayList<>();
        lp.add(p2);
        PersonalGoalCard pg = new PersonalGoalCard(lp);
        String nick = "User101";
        Player p = new Player(nick,pg);
        assertTrue(p.getTilesPicked().isEmpty());
        Tile t1 = new Tile(ItemType.BOOK);

        p.addTilesPicked(t1);
        assertTrue(p.getTilesPicked().contains(t1));
        p.removeTilesPicked(t1);
        assertFalse(p.getTilesPicked().contains(t1));
/*
        p.removeTilesPicked(t);
        assertFalse(p.getTilesPicked().contains(t1));
        assertFalse(p.getTilesPicked().contains(t2));
 */
    }

    @Test
    public void testSetterPlayer() {
        Pair<Pair<Integer,Integer>,Tile> p2 = new Pair<>(new Pair<>(0,0),new Tile(ItemType.BOOK));
        List<Pair<Pair<Integer,Integer>,Tile>> lp = new ArrayList<>();
        lp.add(p2);
        PersonalGoalCard pg = new PersonalGoalCard(lp);
        String nick = "User101";
        Player p = new Player(nick,pg);
        assertFalse(p.getHasFinalToken());
        p.setHasFinalToken(true);
        assertTrue(p.getHasFinalToken());
        List<Tile> t = new ArrayList<>();
        Tile t1 = new Tile(ItemType.BOOK);
        Tile t2 = new Tile(ItemType.CAT);
        t.add(t1);
        t.add(t2);
        p.setTilesPicked(t);
        assertTrue(p.getTilesPicked().get(0).equals(t1));
        assertTrue(p.getTilesPicked().get(1).equals(t2));
        t.remove(t1);
        assertFalse(p.getTilesPicked().get(0).equals(t1));
        assertTrue(p.getTilesPicked().get(0).equals(t2));
    }



}