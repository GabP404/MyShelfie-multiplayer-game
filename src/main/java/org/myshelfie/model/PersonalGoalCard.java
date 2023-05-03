package org.myshelfie.model;
import org.myshelfie.model.util.Pair;
import org.myshelfie.controller.Configuration;

import java.util.List;
import java.util.Map;

/**
 * Class that represents a personal goal card.
 */
public class PersonalGoalCard {

    private List<Pair<Pair<Integer, Integer>, Tile>> constraints;
    private static final Map<Integer, Integer> points_map = Configuration.getPersonalGoalPoints();

    /**
     * Constructor of the PersonalGoalCard class.
     * @param constraint_tiles  The list of tiles that must be present in the bookshelf to satisfy the goal
     */
    public PersonalGoalCard(List<Pair<Pair<Integer, Integer>, Tile>> constraint_tiles) {
        constraints = constraint_tiles;
    }

    /**
     * Get the points that the player gets for completing the Personal Goal.
     * @param shelf The bookshelf
     * @return The number of points
     */
    public int getPoints(Bookshelf shelf) {
        int occurrences = 0;
        for (Pair<Pair<Integer, Integer>, Tile> c: constraints) {
            int col = c.getLeft().getLeft();
            int row = c.getLeft().getRight();
            try {
                Tile t = shelf.getTile(row, col);
                //Count the number of tiles that satisfy the constraint
                if (t != null && t.getItemType() == c.getRight().getItemType()) {
                    occurrences += 1;
                }
            } catch (WrongArgumentException e) {
                //Just do not add points
            }
        }
        return points_map.get(occurrences);
    }
}
