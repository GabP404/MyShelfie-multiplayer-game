package org.myshelfie.network.messages.gameMessages;

import org.myshelfie.controller.Configuration;
import org.myshelfie.model.Board;
import org.myshelfie.model.Tile;

import java.io.Serializable;

/**
 * Immutable version of the {@link Board}, used to send the board to the clients.
 */
public final class ImmutableBoard implements Serializable {
    private final static int[][] mask = Configuration.getBoardMask();
    public final int DIMBOARD;
    private final Tile[][] boardTiles;

    public ImmutableBoard(Board board) {
        this.DIMBOARD = Board.DIMBOARD;
        this.boardTiles = new Tile[DIMBOARD][DIMBOARD];
        for (int i = 0; i < DIMBOARD; i++) {
            for (int j = 0; j < DIMBOARD; j++) {
                this.boardTiles[i][j] = board.getTile(i,j);
            }
        }
    }

    /**
     * @return A copy of the board
     */
    public Tile[][] getBoard() {
        Tile[][] mat = new Tile[DIMBOARD][DIMBOARD];
        for (int i = 0; i < DIMBOARD; i++) {
            for (int j = 0; j < DIMBOARD; j++) {
                mat[i][j] = this.boardTiles[i][j];
            }
        }
        return mat;
    }

    /**
     * @return A copy of the board with the mask applied (used for testing)
     */
    public boolean isRefillNeeded() {
        for (int i = 0; i < DIMBOARD; i++) {
            for (int j = 0; j < DIMBOARD; j++) {
                if (boardTiles[i][j] != null) {
                    if (!hasOneOrMoreFreeBorders(i, j))
                        return false;
                }
            }
        }
        return true;
    }

    /**
     * @param row The row of the tile in the board
     * @param col The column of the tile in the board
     * @return True if the tile has at least one free border, false otherwise
     */
    public boolean hasOneOrMoreFreeBorders(int row, int col) {
        return isFreeTileBox(row - 1, col) || isFreeTileBox(row + 1, col) ||
                isFreeTileBox(row, col - 1) || isFreeTileBox(row, col + 1);
    }

    /**
     * @param row The row of the tile in the board
     * @param col The column of the tile in the board
     * @return True if the tile is free, false otherwise
     */
    private boolean isFreeTileBox(int row, int col) {
        //A cell is considered to be free if it's null or if it's outside the borders,
        if (row < 0 || row >= DIMBOARD || col < 0 || col >= DIMBOARD)
            return true;
        return boardTiles[row][col] == null;
    }

    /**
     * @param x The row of the tile in the board
     * @param y The column of the tile in the board
     * @return The tile in the given position
     */
    public Tile getTile(int x, int y) {
        return this.boardTiles[x][y];
    }

    /**
     * @param x The row of the tile in the board
     * @param y The column of the tile in the board
     * @return The mask of the tile in the given position. This is needed since the available
     *         positions in the board depend on the number of players.
     */
    public static int getMaskItem(int x, int y) {
        return mask[x][y];
    }
}
