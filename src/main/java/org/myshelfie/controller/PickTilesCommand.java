package org.myshelfie.controller;

import org.myshelfie.model.*;
import org.myshelfie.model.util.Pair;
import org.myshelfie.network.messages.commandMessages.PickedTilesCommandMessage;

import java.util.*;
import java.util.stream.Collectors;

public class PickTilesCommand implements Command {
    private Board b;
    private Player currPlayer;
    private List<LocatedTile> tiles;
    private String nickname;
    private ModelState currentModelState;

    public PickTilesCommand(Board b, List<LocatedTile> tiles) {
        this.b = b;
        this.tiles = tiles;
    }

    /**
     * Deserialize the command
     * @param b Board of the game
     * @param currPlayer Current player
     * @param command CommandMessage to deserialize
     * @param currentModelState Current model state
     */
    public PickTilesCommand(Board b, Player currPlayer, PickedTilesCommandMessage command, ModelState currentModelState) throws WrongArgumentException{
        this.b = b;
        this.currPlayer = currPlayer;
        tiles = new ArrayList<>();
        nickname = command.getNickname();

        for (Pair<Integer, Integer> t: command.getTiles()) {
            if (b.getTile(t.getLeft(), t.getRight()) == null || t.getLeft() < 0 || t.getRight() < 0 || t.getRight()>=Board.DIMBOARD || t.getLeft()>=Board.DIMBOARD)
                throw new WrongArgumentException("The tile at row " + t.getLeft() + " and column " + t.getRight() + " does not exist!");
        }

        this.tiles = command.getTiles().stream().map(
                t -> new LocatedTile(
                        b.getTile(t.getLeft(), t.getRight()).getItemType(),
                        b.getTile(t.getLeft(), t.getRight()).getItemId(),
                        t.getLeft(),
                        t.getRight()
                )
        ).collect(Collectors.toList());
        this.currentModelState = currentModelState;
    }

    public boolean isCellSelectable(int row, int col) {
        return b.getTile(row, col) != null && b.hasOneOrMoreFreeBorders(row, col);
    }

    public boolean isCellSelectable(LocatedTile t) {
        return isCellSelectable(t.getRow(), t.getCol());
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
                if (isCellSelectable(row, col)) {
                    selectables.add(new LocatedTile(b.getTile(row, col).getItemType(), row, col));
                }
            }
        }

        return selectables;
    }

    /**
     * Checks whether a group of LocatedTiles forms a valid selection on the board.
     * @param b The Board
     * @param chosen A set containing the tiles that the player wants to select
     * @return True if the chosen LocatedTiles can be selected, false otherwise
     */
    public boolean isTilesGroupSelectable(Board b, Set<LocatedTile> chosen) {
        // Add the check that you cannot select more than 3 tiles
        if (chosen.size() > 3) {
            return false;
        }

        //Check that all the selected tiles are indeed selectable on their own (i.e. at least one free border)
        for (LocatedTile t : chosen) {
            if (!isCellSelectable(t))
                return false;
        }

        // Skip the check if there is only one tile in the selection
        if (chosen.size() < 2) {
            // If so, return true since a single tile or no tiles are always in a line
            return true;
        }

        // The tiles are horizontal / vertical if all the rows / cols are the same
        boolean isHorizontal = chosen.stream().map(LocatedTile::getRow).distinct().count() == 1;
        boolean isVertical = chosen.stream().map(LocatedTile::getCol).distinct().count() == 1;

        if (!isHorizontal && !isVertical)
            return false;

        // Check that the chosen tile are "sequential" i.e., adjacent to each other
        SortedSet<Integer> sortedIndexes = new TreeSet<>();
        if (isHorizontal)
            sortedIndexes.addAll(chosen.stream().map(LocatedTile::getCol).collect(Collectors.toSet()));
        if (isVertical)
            sortedIndexes.addAll(chosen.stream().map(LocatedTile::getRow).collect(Collectors.toSet()));

        return sortedIndexes.last() - sortedIndexes.first() == sortedIndexes.size() - 1;
    }


    public void execute() throws  WrongTurnException, InvalidCommand, WrongArgumentException {
        if (!currPlayer.getNickname().equals(nickname))
            throw new WrongTurnException();
        if (tiles.size() == 0)
            throw new WrongArgumentException("You have to select at least one tile!");
        if(currentModelState != ModelState.WAITING_SELECTION_TILE) throw new InvalidCommand("Waiting for Tile Selection ");

        Set<LocatedTile> tilesSet = new HashSet<>(tiles);
        if (!isTilesGroupSelectable(b, tilesSet))
            throw new WrongArgumentException("The chosen group of tiles is not selectable!");

        for (LocatedTile t: tiles) {
            currPlayer.addTilesPicked(b.getTile(t.getRow(),t.getCol()));
            b.setTile(t.getRow(), t.getCol(), null);
        }

    }
}
