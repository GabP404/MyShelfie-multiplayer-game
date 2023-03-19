package org.myshelfie.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.myshelfie.model.util.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public final class PersonalGoalDeck {
    private List<PersonalGoalCard> cards;
    private static PersonalGoalDeck single_istance;

    /**
     * Initialize the deck of personal goal cards from a specification file.
     * The file contains a list of constratint for each card.
     * The file has this format:
     * {
     *  "cards": [
     *      [
     *          {"col": int, "row": int, "type": string (ItemType)},
     *          {"col": int, "row": int, "type": string (ItemType)},
     *          {"col": int, "row": int, "type": string (ItemType)}
     *      ],
     *      [...],
     *      ...
     *  ]
     * }
     * @throws IOException If the JSON file does not exist
     * @param filename The name of the file which contains the description of the specifics, in JSON.
     */
    private PersonalGoalDeck(String filename) throws IOException {
        this.cards = new ArrayList<PersonalGoalCard>();
        Path filePath = Path.of(filename);
        String jsonString = Files.readString(filePath);
        JSONObject jo = new JSONObject(jsonString);
        JSONArray JSONCards = jo.getJSONArray("cards");
        for (int i = 0; i < JSONCards.length(); i++) {
            JSONArray card = JSONCards.getJSONArray(i);
            PersonalGoalCard c;
            List<Pair<Pair<Integer, Integer>, Tile>> l = new ArrayList<Pair<Pair<Integer, Integer>, Tile>>();
            for (int k = 0; k < card.length(); k++) {
                JSONObject constraint_json = card.getJSONObject(k);
                Pair<Pair<Integer, Integer>, Tile> constraint;
                constraint = new Pair<>(
                        new Pair<Integer, Integer>(
                                (Integer) constraint_json.get("col"),
                                (Integer) constraint_json.get("row")
                        ),
                        new Tile(ItemType.valueOf((String) constraint_json.get("type")))
                );
                l.add(constraint);
            }
            c = new PersonalGoalCard(l);
            cards.add(c);
        }
    }

    /**
     * PersonalGoalDeck constructor, starting from a pre-existing list.
     * @param cardList: The list of PersonalGoalCards.
     */
    private PersonalGoalDeck(List<PersonalGoalCard> cardList) {
        cards = cardList;
    }

    /**
     * Get PersonalGoalDeck instance
     * @param filename Name of the JSON file with the info about the cards
     * @return An instance of the PersonalGoalDeck
     * @throws IOException If the file does not exist
     */
    public static PersonalGoalDeck getInstance(String filename) throws IOException {
        if (single_istance == null)
            single_istance = new PersonalGoalDeck(filename);
        return single_istance;
    }

    /**
     * Get PersonalGoalDeck instance
     * @param cardList List of PersonalGoalCards
     * @return An instance of the PersonalGoalDeck
     */
    public static PersonalGoalDeck getInstance(List<PersonalGoalCard> cardList) {
        if (single_istance == null)
            single_istance = new PersonalGoalDeck(cardList);
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
        List<PersonalGoalCard> drawnCards = new ArrayList<PersonalGoalCard>();
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
