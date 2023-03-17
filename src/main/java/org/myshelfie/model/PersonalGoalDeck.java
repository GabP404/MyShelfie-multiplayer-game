package org.myshelfie.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.myshelfie.model.util.Pair;
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
                PersonalGoalCard c;
		        List<Pair<Pair<Integer, Integer>, Tile>> l = new List<Pair<Pair<Integer, Integer>, Tile>>();
                for (int k = 0; k < card.length(); k++) {
                    JSONObject constraint_json = card.getJSONObject(i);
                    Pair<Pair<Integer, Integer>, Tile> constraint;
                    constraint = new Pair<>(
                        new Pair<Integer, Integer>(
                                (Integer) constraint_json.get("col"),
                                (Integer) constraint_json.get("row")
                        ),
                        new Tile((ItemType) constraint_json.get("type"))
                    );
		            l.add(constraint);
                }
		c = new PersonalGoalCard(l);
                cards.add(c);
            }
        } catch (IOException | TileInsertionException e) {
            //TODO handle exception(s)
        }

        //Shuffle the deck
        this.shuffle();
    }

    public PersonalGoalDeck(List<PersonalGoalCard> cardList) {
        cards = cardList;
        this.shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Returns a card from the deck, without removing it.
     * @return The personal goal card
     */
    public PersonalGoalCard draw() {
	this.shuffle();
        return cards.get(0);
    }

    /**
     * Returns two cards from the deck, without removing them.
     * @return A list with two personal goal cards
     */
    public List<PersonalGoalCard> drawTwo() {
    	List<PersonalGoalCard> l = new ArrayList<PersonalGoalCard>();
        this.shuffle();
        l.add(cards.get(0));
        l.add(cards.get(1));
        return l;
    }
}
