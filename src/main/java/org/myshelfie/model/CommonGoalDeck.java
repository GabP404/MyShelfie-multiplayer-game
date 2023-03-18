package org.myshelfie.model;

import java.util.*;
import java.util.stream.Collectors;

public final class CommonGoalDeck {

    private static CommonGoalDeck single_istance = null;
    private List<CommonGoalCard> deck;

    /**
     * Initialize the deck list so that it contains all CommonGoalCards
     */
    private CommonGoalDeck(List<CommonGoalCard> deck) {
        this.deck = new ArrayList<>(deck);
    }

    public static synchronized CommonGoalDeck getInstance(List<CommonGoalCard> deck) {
        if (single_istance == null)
            single_istance = new CommonGoalDeck(deck);
        return single_istance;
    }

    public List<CommonGoalCard> drawCommonGoalCard(int x) {
        List<CommonGoalCard> drawnCards = new ArrayList<CommonGoalCard>();
        List<Integer> positions= new Random().ints(0, deck.size())
                .distinct()
                .limit(x)
                .boxed()
                .collect(Collectors.toList());
        for(Integer i: positions) {
            drawnCards.add(deck.get(i));
        }
        return drawnCards;
    }
}
