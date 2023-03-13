package org.myshelfie.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommonGoalDeck {
    private List<CommonGoalCard> deck = new ArrayList<>();

    /**
     * Initialize the deck list so that it contains all CommonGoalCards
     */
    public CommonGoalDeck() {
        // TODO: implement this
    }
    public CommonGoalCard drawCommonGoalCard() {
        Collections.shuffle(deck);
        return deck.remove(0);
    }
}
