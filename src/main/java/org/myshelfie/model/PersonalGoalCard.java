package org.myshelfie.model;
import org.myshelfie.model.util.Pair;
import org.myshelfie.controller.Configuration;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Class that represents a personal goal card.
 */
public class PersonalGoalCard implements Serializable {
    private final int id;
    private final List<Pair<Pair<Integer, Integer>, Tile>> constraints;
    // Needed to associate the personal goal card object to its image file
    private static final Map<Integer, Integer> points_map = Configuration.getPersonalGoalPoints();

    public PersonalGoalCard(List<Pair<Pair<Integer, Integer>, Tile>> constraint_tiles, int id) {
        constraints = constraint_tiles;
        this.id = id;
    }

    /**
     * Get the points obtained by a player with the given bookshelf
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

    /**
     * Getter for the constraints
     * @return The constraints
     */
    public List<Pair<Pair<Integer, Integer>, Tile>> getConstraints() {
        return constraints;
    }

    /**
     * Getter for the id
     * @return The id
     */
    public int getId() {
        return id;
    }
}
