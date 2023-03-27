package org.myshelfie.model;

import org.myshelfie.controller.Configuration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public final class PersonalGoalDeck {
    private final List<PersonalGoalCard> cards;
    private static PersonalGoalDeck single_istance;

    /**
     * PersonalGoalDeck constructor, starting from a pre-existing list.
     * @param cardList: The list of PersonalGoalCards.
     */
    private PersonalGoalDeck(List<PersonalGoalCard> cardList) {
        cards = cardList;
    }

    /**
     * Get PersonalGoalDeck instance
     * @return An instance of the PersonalGoalDeck
     * @throws IOException If the file does not exist
     */
    public static PersonalGoalDeck getInstance() throws IOException, URISyntaxException {
        if (single_istance == null) {
            List<PersonalGoalCard> cards;
            cards = Configuration.createPersonalGoalDeck();
            single_istance = new PersonalGoalDeck(cards);
        }
        return single_istance;
    }

    /**
     * Draw a variable number of distinct cards from the deck, without
     * removing them
     * @param x The number of cards to be drawn
     * @return A list containing the drawn cards
     */
    public List<PersonalGoalCard> draw(int x) {
        if (x < 0)
            x = 0;
        if (x > cards.size())
            x = cards.size();
        List<PersonalGoalCard> drawnCards = new ArrayList<>();
        List<Integer> positions= new Random().ints(0, cards.size())
                .distinct()
                .limit(x)
                .boxed()
                .collect(Collectors.toList());
        for(Integer i: positions) {
            drawnCards.add(cards.get(i));
        }
        return drawnCards;
    }
}
