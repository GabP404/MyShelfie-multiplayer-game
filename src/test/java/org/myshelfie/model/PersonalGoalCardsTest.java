package org.myshelfie.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.myshelfie.model.util.Pair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Personal Goal Cards and Deck")
public class PersonalGoalCardsTest {
    private static final String FILENAME = "personalGoalCards.json";

    @BeforeAll
    protected static void createJSONFile() throws IOException {
        int NUMCARDS = 12;
        String[] ITEMTYPES = {"CAT", "BOOK", "PLANT", "GAME", "FRAME", "TROPHY"};
        List<List<JSONObject>> cardsList = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < NUMCARDS; i++) {
            Set<Pair<Integer, Integer>> pairs = new HashSet<>();
            List<JSONObject> constraints = new ArrayList<>();

            while (pairs.size() < 4) {
                int col = rand.nextInt(Bookshelf.NUMCOLUMNS);
                int row = rand.nextInt(Bookshelf.NUMROWS);
                pairs.add(new Pair<>(col, row));
            }

            for (Pair<Integer, Integer> pair : pairs) {
                String itemType = ITEMTYPES[rand.nextInt(ITEMTYPES.length)];
                JSONObject constraint = new JSONObject();
                constraint.put("col", pair.getLeft());
                constraint.put("row", pair.getRight());
                constraint.put("type", itemType);
                constraints.add(constraint);
            }

            cardsList.add(constraints);
        }
        JSONObject cards = new JSONObject();
        cards.put("cards", new JSONArray(cardsList));

        try (FileWriter fileWriter = new FileWriter(FILENAME)) {
            fileWriter.write(cards.toString());
        }
    }

    @AfterAll
    protected static void tearDown() {
        File file = new File(FILENAME);
        assertTrue(file.delete());
    }

    @DisplayName("The JSON file does not exist")
    @Test
    protected void JSONFileNotExisting() {
        assertThrows(IOException.class, () -> PersonalGoalDeck.getInstance("invalid_filename.json"));
    }

    @DisplayName("Normal behaviour - create a deck from the JSON file")
    @Test
    protected void createDeckFromJSON() {
        try {
            PersonalGoalDeck deck = PersonalGoalDeck.getInstance(FILENAME);
            assertInstanceOf(PersonalGoalDeck.class, deck);
        } catch (IOException e) {
            fail();
        }
    }

    @DisplayName("Drawing cards from the deck")
    @ParameterizedTest
    @CsvSource({"-1, 0", "0, 0", "5, 5", "126, 12"})
    protected void drawCards(int n_cards, int expected_length) {
        try {
            PersonalGoalDeck deck = PersonalGoalDeck.getInstance(FILENAME);
            List<PersonalGoalCard> drawn = deck.draw(n_cards);
            assertEquals(drawn.size(), expected_length);
        } catch (IOException e) {
            fail();
        }
    }
}