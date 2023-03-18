package org.myshelfie.model.commonGoal;

import org.myshelfie.model.*;
import org.myshelfie.model.util.Pair;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class SameTypeGroupings extends CommonGoalCard {
    private final Integer numGroups;
    private final Integer groupDim;
    private final List<Pair<Integer, Integer>> adjacencyDeltas = new ArrayList<>();
    /**
     * marks is a matrix that supports bookshelf exploration
     */
    private CellMarkType[][] marks;

    /**
     * Enumeration used to mark cells in three ways during the check
     */
    private enum CellMarkType {
        DONE,
        VISITED,
        NEW
    }

    /**
     * Initialize the CommonGoalCard associating the points' stack to it
     * @param id The identifier of the card
     * @param tokens The token stack that will be placed on the card
     *               NOTE: the stack's generation logic will be in the controller
     * @param numGroups Number of groups required by card
     * @param groupDim Minimum number of tiles of same type required to form a group
     */
    public SameTypeGroupings(String id, ArrayDeque<ScoringToken> tokens, Integer numGroups, Integer groupDim) {
        super(id, tokens);
        this.numGroups = numGroups;
        this.groupDim = groupDim;
        // Initializes the adjacencyDeltas list
        adjacencyDeltas.add(new Pair<>(1, 0));
        adjacencyDeltas.add(new Pair<>(-1, 0));
        adjacencyDeltas.add(new Pair<>(0, 1));
        adjacencyDeltas.add(new Pair<>(0, -1));
    }

    /**
     * Check if the specific constraints of the card are satisfied
     * @param bookshelf The library that will be checked
     */
    @Override
    public Boolean checkGoalSatisfied(Bookshelf bookshelf){
        int numGroupsFound = 0;
        marksInit(bookshelf);
        for (int r = 0; r< Bookshelf.NUMROWS; r++) {
            for (int c = 0; c< Bookshelf.NUMCOLUMNS; c++) {
                if (marks[r][c]!=CellMarkType.DONE) {
                    List<Pair<Integer,Integer>> group = new ArrayList<>();
                    group.add(new Pair<>(r,c));
                    group.addAll(getSameTypeRemainingNeighbours(bookshelf, new Pair<>(r,c)));
                    // while there's at least one element VISITED but not DONE
                    while(group.stream().anyMatch(p -> marks[p.getLeft()][p.getRight()]==CellMarkType.VISITED)) {
                        // create temporary list
                        List<Pair<Integer,Integer>> tmp = new ArrayList<>();
                        // for each non-DONE pair in the group, add to tmp its remaining same-type neighbours
                        for (Pair<Integer,Integer> p : group) {
                            if (marks[p.getLeft()][p.getRight()]!=CellMarkType.DONE) {
                                tmp.addAll(getSameTypeRemainingNeighbours(bookshelf, new Pair<>(p.getLeft(),p.getRight())));
                            }
                        }
                        // add all the pairs to the group
                        group.addAll(tmp);
                    }
                    if (group.size() >= groupDim) {
                        numGroupsFound++;
                    }
                    // increase number of groups found if big enough
                    numGroupsFound += group.size() >= groupDim ? 1 : 0;
                    if (numGroupsFound >= numGroups) {
                        return Boolean.TRUE;
                    }
                }
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Retrieves coordinates of those tiles that are adjacent to the one in position coords and have the same type of it.
     * Marks as VISITED all the cells that are returned, and marks as DONE the cell indexed by coords parameter.
     * @param bookshelf Bookshelf under analysis
     * @param coords Coordinates of the tile under analysis
     * @return a list containing coordinates of remaining adjacent tiles of the same type of the one under analysis, null if the res list is empty
     */
    private List<Pair<Integer,Integer>> getSameTypeRemainingNeighbours(Bookshelf bookshelf, Pair<Integer,Integer> coords) {
        List<Pair<Integer, Integer>> res = new ArrayList<>();
        final int r = coords.getLeft();
        final int c = coords.getRight();
        try {
            ItemType targetType = bookshelf.getTile(r, c).getItemType();
            // try all the 4 adjacent tiles to the reference one
            for (Pair<Integer,Integer> d : adjacencyDeltas) {
                int x = r + d.getLeft();
                int y = c + d.getRight();
                try {
                    if( x>=0 && x< Bookshelf.NUMROWS && y>=0 && y< Bookshelf.NUMCOLUMNS && marks[x][y]==CellMarkType.NEW && bookshelf.getTile(x, y).getItemType() == targetType ) {
                        // add the coordinates to the result list
                        res.add(new Pair<>(x,y));
                        // marks the cells as VISITED
                        marks[x][y] = CellMarkType.VISITED;
                    }
                } catch (TileUnreachableException outOfRangeNeighbour) {
                    // do nothing
                }
            }
        } catch (TileUnreachableException outOfRangeTargetTile) {
            // reference tile has outOfIndex coordinates
            // TODO: decide whether to propagate the exception or not
        }
        // finally marks as DONE the reference tile
        marks[r][c] = CellMarkType.DONE;
        return res;
    }

    /**
     * Support method used every check to initialize the support matrix
     * @param b is used to determine whether a cell is empty or not (if getter returns null set mark to DONE
     *          to avoid considering empty cells during neighbours exploration)
     */
    private void marksInit(Bookshelf b) {
        // TODO: test if setting to DONE empty cells leads to expected behavior
        marks = new CellMarkType[Bookshelf.NUMROWS][Bookshelf.NUMCOLUMNS];
        for (int r = 0; r<Bookshelf.NUMROWS; r++) {
            for (int c = 0; c< Bookshelf.NUMCOLUMNS; c++) {
                try {
                    if (b.getTile(r,c) == null) {
                        marks[r][c] = CellMarkType.DONE;
                    } else {
                        marks[r][c] = CellMarkType.NEW;
                    }
                } catch (TileUnreachableException e) {
                    //
                }
            }
        }
    }
}
