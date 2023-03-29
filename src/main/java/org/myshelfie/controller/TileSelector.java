package org.myshelfie.controller;

import org.myshelfie.model.Board;
import org.myshelfie.model.Bookshelf;
import org.myshelfie.model.LocatedTile;
import org.myshelfie.model.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class TileSelector {
    private boolean isCellSelectable(Board b, int row, int col) {
        return b.getTile(row, col) != null && b.hasOneOrMoreFreeBorders(row, col);
    }

    /**
     *
     * @param b The board of the game
     * @return List of the LocatedTiles that can be selected on the board
     */
    public List<LocatedTile> getSelectable(Board b) {
        List<LocatedTile> selectables = new ArrayList<>();
        for (int row = 0; row < Bookshelf.NUMROWS; row++) {
            for (int col = 0; col < Bookshelf.NUMCOLUMNS; col++) {
                if (isCellSelectable(b, row, col)) {
                    selectables.add(new LocatedTile(b.getTile(row, col).getItemType(), row, col));
                }
            }
        }

        return selectables;
    }

    public boolean selectTilesGroup(Board b, List<LocatedTile> chosen) {
        List<LocatedTile> selectables = getSelectable(b);
        for (LocatedTile t: chosen) {
            if (!selectables.contains(t))
                return false;
        }

        // Check if there are fewer than two tiles in the list
        if (chosen.size() < 2) {
            // If so, return true since a single tile or no tiles are always in a line
            return true;
        }

        // Get the coordinates of the first tile
        Pair<Integer, Integer> first = chosen.get(0).getCoordinates();

        // Initialize variables to store if the tiles are in a horizontal or vertical line
        boolean isHorizontal = true;
        boolean isVertical = true;

        // Loop through the remaining tiles
        for (int i = 1; i < chosen.size(); i++) {
            // Get the coordinates of the current tile
            Pair<Integer, Integer> current = chosen.get(i).getCoordinates();

            // If the rows are not the same, the tiles are not in a horizontal line
            if (first.getLeft().equals(current.getLeft()))
                isHorizontal = false;

            // If the columns are not the same, the tiles are not in a vertical line
            if (first.getRight().equals(current.getRight()))
                isVertical = false;

            // If the tiles are in a horizontal line, check if the column of the current tile is adjacent to the column of the first tile
            if (isHorizontal && Math.abs(first.getRight() - current.getRight()) != 1) {
                isHorizontal = false;
            }

            // If the tiles are in a vertical line, check if the row of the current tile is adjacent to the row of the first tile
            if (isVertical && Math.abs(first.getLeft() - current.getLeft()) != 1) {
                isVertical = false;
            }
        }

        // Return true if the tiles are in a horizontal or vertical line
        return isHorizontal || isVertical;
    }
}
