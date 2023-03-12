package org.myshelfie.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.json.*;

public class PersonalGoalDeck {
    public List<PersonalGoalCard> cards;

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
     * @param filename The name of the file which contains the description of the specifics, in JSON.
     */
    public PersonalGoalDeck(String filename) {
        this.cards = new ArrayList<PersonalGoalCard>();
        Path filePath = Path.of(filename);
        try {
            String jsonString = Files.readString(filePath);
            JSONObject jo = new JSONObject(jsonString);
            JSONArray JSONCards = jo.getJSONArray("cards");
            for (int i = 0; i < JSONCards.length(); i++) {
                JSONArray card = JSONCards.getJSONArray(i);
                PersonalGoalCard c = new PersonalGoalCard();
                for (int k = 0; k < card.length(); k++) {
                    JSONObject constraint = card.getJSONObject(i);
                    c.setTile(
                            (Integer) constraint.get("col"),
                            (Integer) constraint.get("row"),
                            (ItemType) constraint.get("type")
                    );
                }
                cards.add(c);
            }
        } catch (IOException | TileInsertionException e) {
            //TODO handle exception(s)
        }

        //Shuffle the deck
        Collections.shuffle(cards);
    }

    /**
     * Returns a card from the deck, removing it.
     * @return The personal goal card
     */
    public PersonalGoalCard draw() {
        return cards.remove(0);
    }
}
