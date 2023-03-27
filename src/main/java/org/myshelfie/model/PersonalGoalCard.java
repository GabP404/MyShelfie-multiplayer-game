package org.myshelfie.model;
import org.myshelfie.model.util.Pair;

import java.util.List;

public class PersonalGoalCard {
    private List<Pair<Pair<Integer, Integer>, Tile>> constraints;

    public PersonalGoalCard(List<Pair<Pair<Integer, Integer>, Tile>> constraint_tiles) {
        constraints = constraint_tiles;
    }

    public boolean isGoalSatisfied(Bookshelf shelf) {
        for (Pair<Pair<Integer, Integer>, Tile> c: constraints) {
            int col = c.getLeft().getLeft();
            int row = c.getLeft().getRight();
            try {
                Tile t = shelf.getTile(row, col);
                //If there is a tile in the list that is not present in the bookshelf, the goal is unsatisfied
                if (t == null || t.getItemType() != c.getRight().getItemType()) {
                    return false;
                }
            } catch (TileUnreachableException e) {
                return false;
            }
        }
        return true;
    }
}
