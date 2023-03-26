package org.myshelfie.model;

import java.util.ArrayDeque;

public class CreateScoringToken {

    private int numPlayer;

    public CreateScoringToken(int numPlayer) {
        this.numPlayer = numPlayer;
    }

    public ArrayDeque<ScoringToken> createTokensPersonalGoalCard(String id) {
        ArrayDeque<ScoringToken> tokens = new ArrayDeque<>();
        switch (numPlayer) {
            case 2:
                tokens.add(new ScoringToken(4,id));
                tokens.add(new ScoringToken(8,id));
                break;

            case 3:
                tokens.add(new ScoringToken(4,id));
                tokens.add(new ScoringToken(6,id));
                tokens.add(new ScoringToken(8,id));
                break;

            case 4:
                tokens.add(new ScoringToken(2,id));
                tokens.add(new ScoringToken(4,id));
                tokens.add(new ScoringToken(6,id));
                tokens.add(new ScoringToken(8,id));
                break;
        }
        return tokens;
    }
}
