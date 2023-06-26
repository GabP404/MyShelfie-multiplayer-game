package org.myshelfie.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Personal Goal Cards and Deck")
public class PersonalGoalCardsTest {
    @DisplayName("Normal behaviour - create a deck from the JSON file")
    @Test
    protected void createDeckFromJSON() {
        try {
            PersonalGoalDeck deck = PersonalGoalDeck.getInstance();
            assertInstanceOf(PersonalGoalDeck.class, deck);
        } catch (IOException | URISyntaxException e) {
            fail();
        }
    }

    @DisplayName("Checking if PersonalGoal is satisfied or not")
    @Test
    protected void checkGoal() throws IOException, URISyntaxException, WrongArgumentException {
        Bookshelf b = new Bookshelf();
        PersonalGoalDeck deck = PersonalGoalDeck.getInstance();
        PersonalGoalCard card = deck.draw(1).get(0);
        assertEquals(card.getPoints(b), 0);
        assertInstanceOf(Integer.class, card.getId());
        assertDoesNotThrow(() -> card.getConstraints().get(0).getLeft().getLeft());
    }

    @DisplayName("Drawing cards from the deck")
    @ParameterizedTest
    @CsvSource({"-1, 0", "0, 0", "5, 5", "126, 12"})
    protected void drawCards(int n_cards, int expected_length) {
        try {
            PersonalGoalDeck deck = PersonalGoalDeck.getInstance();
            List<PersonalGoalCard> drawn = deck.draw(n_cards);
            assertEquals(drawn.size(), expected_length);
        } catch (IOException | URISyntaxException e) {
            fail();
        }
    }
}