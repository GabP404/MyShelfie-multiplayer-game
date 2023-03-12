package org.myshelfie.model;

import java.util.ArrayDeque;


public abstract class CommonGoalCard {
    private ArrayDeque<ScoringToken> tokens;
    private String ID;

    public abstract Boolean checkGoalSatisfied();
    public String getID() {
        return ID;
    }
    public ScoringToken getTopScoringToken() {
        /*
         *  exposes the top ScoringToken of the stack represented by tokens
         */
        return tokens.peek();
    }
    public ScoringToken popTopScoringToken() {
            return tokens.pop();
    }
}
