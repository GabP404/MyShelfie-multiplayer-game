package org.myshelfie.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.myshelfie.model.ItemType;
import org.myshelfie.model.PersonalGoalCard;
import org.myshelfie.model.Tile;
import org.myshelfie.model.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {
    private static final String JSONfile = "constants.json";

    /**
     * Get the JSON object from the constraint file
     * @return The JSON object
     */
    static private JSONObject getJSON() {
        try {
            InputStream is = Configuration.class.getClassLoader().getResourceAsStream(JSONfile);
            return new JSONObject(new String(is.readAllBytes()));
        } catch (IOException e) {
            return new JSONObject();
        } catch (NullPointerException e) {
            throw new RuntimeException("The file " + JSONfile + " is missing!");
        }
    }

    /**
     * Initialize the deck of personal goal cards from the specification file.
     * The file contains a list of constraint for each card.
     * The file has this format:
     * "personal_goal_cards": {
     *  "cards": [
     *      [
     *          {"col": int, "row": int, "type": string (ItemType)},
     *          {"col": int, "row": int, "type": string (ItemType)},
     *          {"col": int, "row": int, "type": string (ItemType)}
     *      ],
     *      [...],
     *      ...
     *  ],
     *  "map_points": {
     *       "1": 1,
     *       "2": 3,
     *       "3": 6,
     *       "4": 8
     *     }
     * }
     */
    static public List<PersonalGoalCard>  createPersonalGoalDeck() {
        ArrayList<PersonalGoalCard> cards = new ArrayList<>();
        JSONObject jo = getJSON().getJSONObject("personal_goal_cards");
        JSONArray JSONCards = jo.getJSONArray("cards");
        for (int i = 0; i < JSONCards.length(); i++) {
            JSONObject card = JSONCards.getJSONObject(i);
            PersonalGoalCard c;
            int id = card.getInt("id");
            List<Pair<Pair<Integer, Integer>, Tile>> l = new ArrayList<>();
            JSONArray card_positions = card.getJSONArray("content");
            for (int k = 0; k < card_positions.length(); k++) {
                JSONObject single_tile = card_positions.getJSONObject(k);
                Pair<Pair<Integer, Integer>, Tile> constraint;
                constraint = new Pair<>(
                        new Pair<>(
                                (Integer) single_tile.get("col"),
                                (Integer) single_tile.get("row")
                        ),
                        new Tile(ItemType.valueOf((String) single_tile.get("type")))
                );
                l.add(constraint);
            }
            c = new PersonalGoalCard(l, id);
            cards.add(c);
        }
        return cards;
    }

    /**
     * Get a map of the number of completed goals to the number of points
     * @return
     */
    static public Map<Integer, Integer> getPersonalGoalPoints() {
        JSONObject jo = getJSON().getJSONObject("personal_goal_cards");
        JSONObject JSONPoints = jo.getJSONObject("map_points");
        Map<Integer, Integer> points = new HashMap<>();
        for (String key : JSONPoints.keySet()) {
            points.put(Integer.parseInt(key), JSONPoints.getInt(key));
        }
        return points;
    }

    /**
     * Get the number of points that the player gets for completing the game first.
     * @return The number of points
     */
    static int getFinalPoints() {
        return getJSON().getInt("final_points");
    }

    /**
     * Get the board mask from the specification file.
     * The mask specifies how many players have to be present in the game
     * for the board cell to be "active".
     * @return The mask as a 2D array of integers
     */
    static public int[][] getBoardMask() {
        JSONObject jo = getJSON().getJSONObject("board");
        JSONArray JSONMask = jo.getJSONArray("mask");
        int[][] mask = new int[JSONMask.length()][];
        for (int i = 0; i < JSONMask.length(); i++) {
            JSONArray row = JSONMask.getJSONArray(i);
            mask[i] = new int[row.length()];
            for (int k = 0; k < row.length(); k++) {
                mask[i][k] = row.getInt(k);
            }
        }
        return mask;
    }

    /**
     * Get the dimension of the board
     * @return The dimension of the board
     */
    static public int getBoardDimension() {
        JSONObject jo = getJSON().getJSONObject("board");
        return jo.getInt("dimension");
    }

    /**
     * Get the number of rows of the bookshelf
     * @return The number of rows of the bookshelf
     */
    static public int getBookshelfRows() {
        JSONObject jo = getJSON().getJSONObject("bookshelf");
        return jo.getInt("rows");
    }

    /**
     * Get the number of columns of the bookshelf
     * @return The number of columns of the bookshelf
     */
    static public int getBookshelfCols() {
        JSONObject jo = getJSON().getJSONObject("bookshelf");
        return jo.getInt("cols");
    }

    /**
     * Get the number of tiles to insert in the tile bag per type (thropies, books, ...)
     * @return The number of tiles per type
     */
    static public int getTilesPerType() {
        JSONObject jo = getJSON().getJSONObject("tile_bag");
        return jo.getInt("tiles_per_type");
    }


    /**
     * Get the timeout of the timer in order to handle the disconnection of a player
     * @return The timeout of the timer in millisecond
     */
    static public int getTimerTimeout() {
        return getJSON().getInt("timer_timeout");
    }

    /**
     * Get the points that a player gets for a group of adjacent tiles of the same type
     * @return Mapping between the number of tiles of the same type adjacent and the number of points
     */
    static public HashMap<Integer, Integer> getMapPointsGroup() {
        JSONObject JSONPoints = getJSON().getJSONObject("map_points_group");
        HashMap<Integer, Integer> points = new HashMap<>();
        for (String key : JSONPoints.keySet()) {
            points.put(Integer.parseInt(key), JSONPoints.getInt(key));
        }
        return points;
    }
}
