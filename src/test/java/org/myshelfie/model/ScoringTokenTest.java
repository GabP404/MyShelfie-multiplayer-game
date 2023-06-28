package org.myshelfie.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

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
