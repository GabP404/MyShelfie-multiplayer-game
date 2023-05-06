package org.myshelfie.controller;

import org.myshelfie.model.*;
import org.myshelfie.network.messages.commandMessages.PickedTilesCommandMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PickTilesCommand implements Command {
    private Board b;
    private Player currPlayer;
    private Set<LocatedTile> tiles;
    private String nickname;
    private ModelState currentModelState;

    public PickTilesCommand(Board b, Set<LocatedTile> tiles) {
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
    public PickTilesCommand(Board b, Player currPlayer, PickedTilesCommandMessage command, ModelState currentModelState) {
        this.b = b;
        this.currPlayer = currPlayer;

        nickname = command.getNickname();

        this.tiles = command.getTiles().stream().map(
                t -> new LocatedTile(
                        b.getTile(t.getLeft(), t.getRight()).getItemType(),
                        t.getLeft(),
                        t.getRight()
                )
        ).collect(Collectors.toSet());
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
        //Check that all the selected tiles are indeed selectable on their own (i.e. at least one free border)
        for (LocatedTile t: chosen) {
            if (!isCellSelectable(t))
                return false;
        }

        // Check if there are fewer than two tiles in the list
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
        List<Integer> l = null;
        if (isHorizontal)
            l = chosen.stream().map(LocatedTile::getCol).toList();
        if (isVertical)
            l = chosen.stream().map(LocatedTile::getRow).toList();

        for (int i = 0; i < l.size() - 1; i++) {
            if (l.get(i + 1) - l.get(i) != 1)
                return false;
        }
        return true;
    }


    public void execute() throws  WrongTurnException, InvalidCommand, WrongArgumentException {
        if(!currPlayer.getNickname().equals(nickname)) {
            throw new WrongTurnException();
        }
        if(currentModelState == ModelState.WAITING_SELECTION_TILE) throw new InvalidCommand("Waiting for Tile Selection ");

        if (!isTilesGroupSelectable(b, tiles))
            throw new WrongArgumentException("The chosen group of tiles is not selectable!");

        for (LocatedTile t: tiles)
        {
            currPlayer.addTilesPicked(b.getTile(t.getRow(),t.getCol()));
            b.setTile(t.getRow(), t.getCol(), null);
        }

    }
}
