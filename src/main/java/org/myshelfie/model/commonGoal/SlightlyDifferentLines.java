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
     * @param direction If false we're looking for ROWS
     *                  if true we're looking for COLUMNS
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
     * Check satisfaction of constraint in the form of: entire rows or entire columns made of tiles with at least numMinTypes different
     * types or with up to numMaxTypes different types.
     * @param bookshelf Bookshelf to be analysed
     * @return Boolean.TRUE if the constraints are satisfied, FALSE if not
     */
    @Override
    public Boolean checkGoalSatisfied(Bookshelf bookshelf){
        int xlim;
        int ylim;
        int linesFound=0;

        if (direction) {
            // we have to iterate the bookshelf looking for COLUMNS, so we want the external loop to iterate on columns
            xlim = Bookshelf.NUMCOLUMNS;
            ylim = Bookshelf.NUMROWS;
        } else {
            // we have to iterate the bookshelf looking for ROWS
            xlim = Bookshelf.NUMROWS;
            ylim = Bookshelf.NUMCOLUMNS;
        }
        // iteration extremes as well as the correlation between iteration variables xy and coordinates depends on direction attribute
        // NOTE: while debugging keep this in mind (it implies that x and y can either refer to rows and columns or to columns and rows)
        for (int x=0; x<xlim; x++) {
            Set<ItemType> diffTypesFound = new HashSet<>();
            for (int y=0; y<ylim; y++) {
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
