package org.myshelfie.network.messages.gameMessages;

import org.myshelfie.model.Board;
import org.myshelfie.model.Tile;

public final class ImmutableBoard {
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

    public Tile[][] getBoard() {
        Tile[][] mat = new Tile[DIMBOARD][DIMBOARD];
        for(int i = 0; i < DIMBOARD; i++) {
            for(int j = 0; j < DIMBOARD; j++) {
                mat[i][j] = this.boardTiles[i][j];
            }
        }
        return mat;
    }

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

    private boolean hasOneOrMoreFreeBorders(int row, int col) {
        return isFreeTileBox(row - 1, col) || isFreeTileBox(row + 1, col) ||
                isFreeTileBox(row, col - 1) || isFreeTileBox(row, col + 1);
    }

    private boolean isFreeTileBox(int row, int col) {
        //A cell is considered to be free if it's null or if it's outside the borders,
        if (row < 0 || row >= DIMBOARD || col < 0 || col >= DIMBOARD)
            return true;
        return boardTiles[row][col] == null;
    }

    public Tile getTile(int x, int y) {
        return this.boardTiles[x][y];
    }


}
