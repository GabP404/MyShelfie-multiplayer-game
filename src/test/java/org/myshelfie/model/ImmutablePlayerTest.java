package org.myshelfie.model;

import org.junit.jupiter.api.Test;
import org.myshelfie.network.messages.gameMessages.ImmutablePlayer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImmutablePlayerTest {
    // NOTE: since these tests address an immutable class, only observable behavior is tested

    @Test
    public void testConstructorAndGetterPlayer() throws IOException, URISyntaxException {
        PersonalGoalDeck pgc = PersonalGoalDeck.getInstance();
        List<PersonalGoalCard> pgcGame = pgc.draw(1);
        PersonalGoalCard pg = pgcGame.get(0);
        String nick = "User101";
        Player p = new Player(nick,pg);
        p.addScoringToken(new ScoringToken(8,"1"));
        p.addScoringToken(new ScoringToken(4,"2"));

        ImmutablePlayer x = new ImmutablePlayer(p);
        assertNotNull(x);
        assertNotNull(x.getBookshelf());
        assertFalse(x.getHasFinalToken());
        assertNotNull(x.getCommonGoalTokens());
        assertNotNull(x.getNickname());
        assertNotNull(x.getTilesPicked());
        assertNotNull(x.getPersonalGoal());
        assertEquals(12, x.getPointsScoringTokens());
    }


}