package org.myshelfie.model;

import org.myshelfie.model.commonGoal.*;

import java.util.*;
import java.util.stream.Collectors;

public class CommonGoalDeck {
    public static final int COMMON_GOAL_DECK_SIZE = 12;
    private int numPlayer;


    /**
     * Initialize the deck list so that it contains all CommonGoalCards
     */
    public CommonGoalDeck(int numPlayer) {
        this.numPlayer = numPlayer;
    }

    public List<CommonGoalCard> drawCommonGoalCard() {
        CreateScoringToken factoryScoringToken = new CreateScoringToken(numPlayer);
        List<CommonGoalCard> drawnCards = new ArrayList<CommonGoalCard>();
        ArrayDeque<ScoringToken> tokens;
        List<Integer> positions= new Random().ints(0, COMMON_GOAL_DECK_SIZE)
                .distinct()
                .limit(this.numPlayer)
                .boxed()
                .collect(Collectors.toList());

        for(Integer i: positions) {
            switch (i) {
                case 1:
                    drawnCards.add(new SameTypeGroupings("1",factoryScoringToken.createTokensPersonalGoalCard("1"),6,2));
                    break;
                case 2:
                    drawnCards.add(new SameTypeGroupings("2",factoryScoringToken.createTokensPersonalGoalCard("2"),4,4));
                    break;
                case 3:
                    drawnCards.add(new EqualCorners("3", factoryScoringToken.createTokensPersonalGoalCard("3")));
                    break;
                case 4:
                    drawnCards.add(new SquareTiles("4", factoryScoringToken.createTokensPersonalGoalCard("4")));
                    break;
                case 5:
                    drawnCards.add(new SlightlyDifferentLines("5", factoryScoringToken.createTokensPersonalGoalCard("5"), true, 3, 1, 3));
                    break;
                case 6:
                    drawnCards.add(new EqualEight("6", factoryScoringToken.createTokensPersonalGoalCard("6")));
                    break;
                case 7:
                    drawnCards.add(new DiagonalTiles("7", factoryScoringToken.createTokensPersonalGoalCard("7")));
                    break;
                case 8:
                    drawnCards.add(new SlightlyDifferentLines("8", factoryScoringToken.createTokensPersonalGoalCard("8"), false, 3, 1, 4));
                    break;
                case 9:
                    drawnCards.add(new SlightlyDifferentLines("9", factoryScoringToken.createTokensPersonalGoalCard("9"), true, 6, 6, 2));
                    break;
                case 10:
                    drawnCards.add(new SlightlyDifferentLines("10", factoryScoringToken.createTokensPersonalGoalCard("10"),  false, 6, 5, 2));
                    break;
                case 11:
                    drawnCards.add(new CrossTiles("11",  factoryScoringToken.createTokensPersonalGoalCard("11")));
                    break;
                case 12:
                    drawnCards.add(new StairTiles("12", factoryScoringToken.createTokensPersonalGoalCard("12")));
                    break;
            }
        }
        return drawnCards;
    }
}
