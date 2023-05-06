package org.myshelfie.network.messages.gameMessages;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.myshelfie.model.PersonalGoalCard;
import org.myshelfie.model.Player;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the gameMessages package
 */
public class GameMessagesTest {
    /**
     * Test for the ImmutablePlayer class
     */
    @Mock PersonalGoalCard personalGoalCard;

    @InjectMocks ImmutablePlayer immutablePlayer;

    @Test
    public void testImmutablePlayer() {
        Player player = new Player("test", personalGoalCard);
        immutablePlayer = new ImmutablePlayer(player);
        assertEquals(immutablePlayer.getNickname(), player.getNickname());
        assertEquals(immutablePlayer.getCommonGoalTokens(),  player.getCommonGoalTokens());
        assertEquals(immutablePlayer.getHasFinalToken(), player.getHasFinalToken());
        assertEquals(immutablePlayer.getPersonalGoal(), player.getPersonalGoal());
        assertInstanceOf(ImmutableBookshelf.class, immutablePlayer.getBookshelf());
        assertEquals(immutablePlayer.getTilesPicked(), player.getTilesPicked());
        assert (immutablePlayer.getPointsScoringTokens() >= 0);
    }
}
