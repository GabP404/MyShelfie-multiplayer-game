package org.myshelfie.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/*
Questi test coprono tutte le linee di codice della classe ScoringToken.
 Il test "testConstructorAndGetters" copre il costruttore e i metodi getPoints e getCommonGoalId,
 mentre i test "testGetPoints" e "testGetCommonGoalId" coprono i singoli metodi getPoints e getCommonGoalId.
 */

public class ScoringTokenTest {

    @Test
    public void testConstructorAndGetters() {
        Integer points = 5;
        String commonGoalId = "CG1";
        ScoringToken token = new ScoringToken(points, commonGoalId);
        assertEquals(points, token.getPoints());
        assertEquals(commonGoalId, token.getCommonGoalId());
    }

    @Test
    public void testGetPoints() {
        Integer points = 5;
        String commonGoalId = "CG1";
        ScoringToken token = new ScoringToken(points, commonGoalId);
        assertEquals(points, token.getPoints());
    }

    @Test
    public void testGetCommonGoalId() {
        Integer points = 5;
        String commonGoalId = "CG1";
        ScoringToken token = new ScoringToken(points, commonGoalId);
        assertEquals(commonGoalId, token.getCommonGoalId());
    }
}
