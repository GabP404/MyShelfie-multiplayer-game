package org.myshelfie.controller;

import org.myshelfie.model.*;
import org.myshelfie.model.util.Pair;
import org.myshelfie.network.messages.commandMessages.PickedTilesCommandMessage;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class implements a command (following the command design pattern) that picks tiles from the board.
 */
public class PickTilesCommand implements Command {
    private final Board board;
    private Player currPlayer;
    private List<LocatedTile> tiles;
    private String nickname;
    private ModelState currentModelState;

    public PickTilesCommand(Board board, List<LocatedTile> tiles) {
        this.board = board;
        this.tiles = tiles;
    }

    /**
     * Construct the command deserializing a {@link PickedTilesCommandMessage}
     * @param board Board of the game
     * @param currPlayer Current player
     * @param command CommandMessage to deserialize
     * @param currentModelState Current model state
     */
    public PickTilesCommand(Board board, Player currPlayer, PickedTilesCommandMessage command, ModelState currentModelState) throws WrongArgumentException{
        this.board = board;
        this.currPlayer = currPlayer;
        tiles = new ArrayList<>();
        nickname = command.getNickname();

        for (Pair<Integer, Integer> t: command.getTiles()) {
            if (board.getTile(t.getLeft(), t.getRight()) == null || t.getLeft() < 0 || t.getRight() < 0 || t.getRight()>=Board.DIMBOARD || t.getLeft()>=Board.DIMBOARD)
                throw new WrongArgumentException("The tile at row " + t.getLeft() + " and column " + t.getRight() + " does not exist!");
        }

        this.tiles = command.getTiles().stream().map(
                t -> new LocatedTile(
                        board.getTile(t.getLeft(), t.getRight()).getItemType(),
                        board.getTile(t.getLeft(), t.getRight()).getItemId(),
                        t.getLeft(),
                        t.getRight()
                )
        ).collect(Collectors.toList());
        this.currentModelState = currentModelState;
    }


    /**
     * Utility method used to check if a tile is selectable on the board
     * @param row Row of the tile
     * @param col Column of the tile
     * @return True if the tile is selectable, false otherwise
     */
    public boolean isCellSelectable(int row, int col) {
        return board.getTile(row, col) != null && board.hasOneOrMoreFreeBorders(row, col);
    }

    /**
     * Utility method used to check if a LocatedTile is selectable on the board
     * @param t LocatedTile to check
     * @return True if the tile is selectable, false otherwise
     */
    public boolean isCellSelectable(LocatedTile t) {
        return isCellSelectable(t.getRow(), t.getCol());
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


    /**
     * First checks if the chosen tiles are valid, then adds them to the player's hand, while removing them from the board.
     * @throws WrongTurnException If it's not the player's turn
     * @throws InvalidCommandException If the command is not valid in the current state
     * @throws WrongArgumentException If the chosen tiles are not valid (don't form a valid selection, are to many for the bookshelf)
     */
    public void execute() throws  WrongTurnException, InvalidCommandException, WrongArgumentException {
        if (!currPlayer.getNickname().equals(nickname))
            throw new WrongTurnException();
        if (tiles.size() == 0)
            throw new WrongArgumentException("You have to select at least one tile!");
        if(currentModelState != ModelState.WAITING_SELECTION_TILE) throw new InvalidCommandException("Waiting for Tile Selection ");

        Set<LocatedTile> tilesSet = new HashSet<>(tiles);
        if (!isTilesGroupSelectable(board, tilesSet))
            throw new WrongArgumentException("The chosen group of tiles is not selectable!");

        if(tilesSet.size() + currPlayer.getBookshelf().getMinHeight() > Bookshelf.NUMROWS) throw new WrongArgumentException("You can't pick that many tiles");

        for (LocatedTile t: tiles) {
            currPlayer.addTilesPicked(t);
            board.setTile(t.getRow(), t.getCol(), null);
        }

    }
}
