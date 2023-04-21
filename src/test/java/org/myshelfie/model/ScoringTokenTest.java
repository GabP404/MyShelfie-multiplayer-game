package org.myshelfie.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/*
Questi test coprono tutte le linee di codice della classe ScoringToken.
 Il test "testConstructorAndGetters" copre il costruttore e i metodi getPoints e getCommonGoalId,
 mentre i test "testGetPoints" e "testGetCommonGoalId" coprono i singoli metodi getPoints e getCommonGoalId.
 */

@DisplayName("ScoringToken")
public class ScoringTokenTest {

    @Test
    public void testConstructorAndGetters() throws IOException, URISyntaxException {
       CommonGoalDeck x = CommonGoalDeck.getInstance();
       List<CommonGoalCard> com = x.drawCommonGoalCard(1);
        ScoringToken token = new ScoringToken(8,com.get(0).getId());
        assertEquals(8, token.getPoints());
        assertEquals(com.get(0).getId(), token.getCommonGoalId());
    }

}
