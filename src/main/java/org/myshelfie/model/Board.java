package org.myshelfie.model;

public class Board {
    /**
     * Describes the board on which the tiles are placed.
     */

    //Declare the minimum number of players that have to play so that
    //the corresponding box is activated during the game
    private static final int[][] mask = {
        {5, 5, 5, 3, 4, 5, 5, 5, 5},
        {5, 5, 5, 2, 2, 4, 5, 5, 5},
        {5, 5, 3, 2, 2, 2, 3, 5, 5},
        {5, 4, 2, 2, 2, 2, 2, 2, 3},
        {4, 2, 2, 2, 2, 2, 2, 2, 4},
        {3, 2, 2, 2, 2, 2, 2, 4, 5},
        {5, 5, 3, 2, 2, 2, 3, 5, 5},
        {5, 5, 5, 4, 2, 2, 5, 5, 5},
        {5, 5, 5, 5, 4, 3, 5, 5, 5},
    };
    public static final int DIMBOARD = 9;
    private Tile[][] boardTiles;

    /**
     * Constructor of the Board class.
     * Will set all the Tiles to null.
     */
    public Board() {
        this.boardTiles = new Tile[DIMBOARD][DIMBOARD];
        applyMask(0);
    }

    /**
     * Constructor of the board class.
     * Depending on the number of the players, only enables certain Tile boxes
     * (and set the others to null)
     * @param numPlayers: The number of players in the game
     */
    public Board(int numPlayers) {
        this.boardTiles = new Tile[DIMBOARD][DIMBOARD];
        applyMask(numPlayers);
    }

    /**
     * Set to null the tile boxes in the board that should not be used in the game,
     * depending on the number of players.
     * @param numPlayers: The number of players in the game
     */
    private void applyMask(int numPlayers) {
        //First of all, enable only the tiles that can be used in a 4-player game
        for (int i = 0; i < DIMBOARD; i++) {
            for (int j = 0; j < DIMBOARD; j++) {
                if (numPlayers < mask[i][j])
                    boardTiles[i][j] = null;
            }
        }
    }

    /**
     * Refill the board.
     * @param numPlayers The number of players
     * @param bag The bag of Tiles
     */
    public void refillBoard(int numPlayers, TileBag bag) {
        for (int i = 0; i < DIMBOARD; i++) {
            for (int j = 0; j < DIMBOARD; j++) {
                if (numPlayers >= mask[i][j] && boardTiles[i][j] == null) {
                    boardTiles[i][j] = bag.drawItemTile();
                }
            }
        }
    }

    /**
     * Check whether the board needs to be refilled or not
     * @return true if the board needs to be refilled, false otherwise
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

    public void setTile(int x, int y, Tile t) {
        this.boardTiles[x][y] = t;
    }

    public Tile getTile(int x, int y) {
        return this.boardTiles[x][y];
    }
}
