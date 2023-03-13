package org.myshelfie.model;

import java.util.ArrayDeque;


public abstract class CommonGoalCard {
    private ArrayDeque<ScoringToken> tokens;
    private String id;

    /**
     * Initialize the CommonGoalCard associating the points' stack to it
     * @param tokens The token stack that will be placed on the card
     *               NOTE: the stack's generation logic will be in the controller
     */
    public CommonGoalCard(String id, ArrayDeque<ScoringToken> tokens){
        this.id = id;
        this.tokens = tokens;
    }

    public abstract Boolean checkGoalSatisfied(Bookshelf bookshelf);

    public String getId() {
        return id;
    }

    /**
     *  Returns the top ScoringToken of the stack without removing it
     */
    public ScoringToken getTopScoringToken() {
        return tokens.peek();
    }

    public ScoringToken popTopScoringToken() {
            return tokens.pop();
    }
}
