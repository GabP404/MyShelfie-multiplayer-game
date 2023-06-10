package org.myshelfie.network.messages.gameMessages;

import org.junit.jupiter.api.Test;
import org.myshelfie.controller.Configuration;
import org.myshelfie.model.PersonalGoalCard;
import org.myshelfie.model.Player;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Unit tests for the gameMessages package
 */
public class GameMessagesTest {

    @Test
    public void testImmutablePlayer() {
        List<PersonalGoalCard> deck = Configuration.createPersonalGoalDeck();
        PersonalGoalCard personalGoalCard = deck.get(0);
        Player player = new Player("test", personalGoalCard);
        ImmutablePlayer immutablePlayer = new ImmutablePlayer(player);
        assertEquals(immutablePlayer.getNickname(), player.getNickname());
        assertEquals(immutablePlayer.getCommonGoalTokens(),  player.getCommonGoalTokens());
        assertEquals(immutablePlayer.getHasFinalToken(), player.getHasFinalToken());
        assertEquals(immutablePlayer.getPersonalGoal(), player.getPersonalGoal());
        assertInstanceOf(ImmutableBookshelf.class, immutablePlayer.getBookshelf());
        assertEquals(immutablePlayer.getTilesPicked(), player.getTilesPicked());
        assert (immutablePlayer.getPointsScoringTokens() >= 0);
    }
}
