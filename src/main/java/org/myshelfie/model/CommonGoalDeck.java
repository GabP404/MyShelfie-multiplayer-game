package org.myshelfie.model;

import org.myshelfie.model.commonGoal.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Deck containing all the CommonGoalCards. It is a singleton, thus its constructor is private.
 */
public final class CommonGoalDeck {

    private static CommonGoalDeck single_istance = null;
    private final List<CommonGoalCard> deck;

    /**
     * Initialize the deck list so that it contains all CommonGoalCards.
     */
    private CommonGoalDeck() {
        this.deck = new ArrayList<>();
        this.deck.add(new SameTypeGroupings("1",6,2));
        this.deck.add(new SameTypeGroupings("2",4,4));
        this.deck.add(new EqualCorners("3"));
        this.deck.add(new SquareTiles("4"));
        this.deck.add(new SlightlyDifferentLines("5",true,3,1,3));
        this.deck.add(new EqualEight("6"));
        this.deck.add(new DiagonalTiles("7"));
        this.deck.add(new SlightlyDifferentLines("8", false, 3, 1, 4));
        this.deck.add(new SlightlyDifferentLines("9", true, 6, 6, 2));
        this.deck.add(new SlightlyDifferentLines("10",  false, 6, 5, 2));
        this.deck.add(new CrossTiles("11"));
        this.deck.add(new StairTiles("12"));
    }

    /**
     * Get the instance of the CommonGoalDeck, creating it if it does not exist.
     * @return the instance of the CommonGoalDeck
     */
    public static synchronized CommonGoalDeck getInstance() {
        if (single_istance == null)
            single_istance = new CommonGoalDeck();
        return single_istance;
    }

    /**
     * Get an x-long list of CommonGoalCards, drawn randomly from the deck without repetitions.
     * Drawn cards are not removed from the deck.
     * @param x the number of cards to draw
     * @return the list of drawn cards
     */
    public List<CommonGoalCard> drawCommonGoalCard(int x) {
        List<CommonGoalCard> drawnCards = new ArrayList<>();
        List<Integer> positions= new Random().ints(0, deck.size())
                .distinct()
                .limit(x)
                .boxed().toList();
        for(Integer i: positions) {
            drawnCards.add(deck.get(i));
        }
        return drawnCards;
    }
}
