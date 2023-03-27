package org.myshelfie.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.myshelfie.model.ItemType;
import org.myshelfie.model.PersonalGoalCard;
import org.myshelfie.model.Tile;
import org.myshelfie.model.util.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private static final String personalGoalCardJSONFile = "personalGoalCards.json";
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
     */
    static public List<PersonalGoalCard>  createPersonalGoalDeck() throws IOException {
        ArrayList<PersonalGoalCard> cards = new ArrayList<>();
        Path filePath;
        try {
            filePath = Paths.get(ClassLoader.getSystemResource(personalGoalCardJSONFile).toURI());
        } catch (URISyntaxException e) {
            return new ArrayList<>();
        }
        String jsonString = Files.readString(filePath);
        JSONObject jo = new JSONObject(jsonString);
        JSONArray JSONCards = jo.getJSONArray("cards");
        for (int i = 0; i < JSONCards.length(); i++) {
            JSONArray card = JSONCards.getJSONArray(i);
            PersonalGoalCard c;
            List<Pair<Pair<Integer, Integer>, Tile>> l = new ArrayList<>();
            for (int k = 0; k < card.length(); k++) {
                JSONObject constraint_json = card.getJSONObject(k);
                Pair<Pair<Integer, Integer>, Tile> constraint;
                constraint = new Pair<>(
                        new Pair<>(
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
        return cards;
    }
}
