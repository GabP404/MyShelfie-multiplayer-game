package org.myshelfie.model.commonGoal;

import org.myshelfie.model.*;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class SlightlyDifferentLines extends CommonGoalCard {
    private final boolean direction;
    private final int numMaxDiffTypes;
    private final int numMinDiffTypes;
    private final int numLines;

    /**
     * Constructor of the card
     * @param id Card identifier
     * @param tokens Tokens' stack on the card
     * @param direction If 0 we're looking for ROWS
     *                  if 1 we're looking for COLUMNS
     * @param numMaxDiffTypes Max number of different types of tiles in a group
     * @param numMinDiffTypes Min number of different types of tiles in a group
     * @param numLines Number of lines we're looking for
     */
    public SlightlyDifferentLines(String id, ArrayDeque<ScoringToken> tokens, boolean direction, int numMaxDiffTypes, int numMinDiffTypes, int numLines) {
        super(id, tokens);
        this.direction = direction;
        this.numLines = numLines;
        this.numMaxDiffTypes = numMaxDiffTypes;
        this.numMinDiffTypes = numMinDiffTypes;
    }

    /**
     * Check satisfatction of constaint in the form of: entire rows or entire columns made of tiles with at least numMinTypes different
     * types or with up to numMaxTypes different types.
     * @param bookshelf Bookshelf to be analysed
     * @return Boolean.TRUE if the constraints are satisfied, FALSE if not
     */
    public Boolean checkGoalSatisfied(Bookshelf bookshelf){
        int xlim;
        int ylim;
        int linesFound=0;
        int numCols = 5;
        int numRows = 6;

        if (direction) {
            // we have to iterate the bookshelf looking for COLUMNS, so we want the external loop to iterate on columns
            xlim = numCols;
            ylim = numRows;
        } else {
            // we have to iterate the bookshelf looking for ROWS
            xlim = numRows;
            ylim = numCols;
        }
        // iteration extremes as well as the correlation between iteration variables xy and coordinates depends on direction attribute
        for (int x=0; x<xlim; x++) {
            Set<ItemType> diffTypesFound = new HashSet<>();
            for (int y=0; y<ylim; y++) {
                try {
                    Tile tmp;
                    if (direction) {
                        // we're looking for COLUMNS, so second coordinate must refer to external loop's iterator variable
                        tmp = bookshelf.getTile(y, x);
                    } else {
                        // we're looking for ROWS, so first coordinate must refer to external loop's iterator variable
                        tmp = bookshelf.getTile(x, y);
                    }
                    if (tmp!=null){
                        diffTypesFound.add(tmp.getItemType());
                    } else {
                        // the ROW or COLUMN cannot have empty cells, so if a null cell is found than the entire internal
                        // iteration must be interrupted and discarded
                        diffTypesFound = null;
                        break;
                    }
                } catch(TileUnreachableException e) {
                    // should never
                }
            }
            // if the line's size respects constraint than increments the number of lines found
            if (diffTypesFound != null) {
                linesFound += (diffTypesFound.size() <= numMaxDiffTypes && diffTypesFound.size()>=numMinDiffTypes) ? 1 : 0;
            }
            if (linesFound >= numLines) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
