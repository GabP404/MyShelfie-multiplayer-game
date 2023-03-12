package org.myshelfie.model;

import java.util.Collections;
import java.util.LinkedList;

public class CommonGoalDeck {
    private LinkedList<CommonGoalCard> deck;
    public CommonGoalDeck() {
        /*
         * Initialize the deck list so that it contains all CommonGoalCards
         */
    }
    public CommonGoalCard drawCommonGoalCard() {
        Collections.shuffle(deck);
        return deck.pop();
    }
}
