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
    private static final int DIMBOARD = 9;
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

    public void setTile(int x, int y, Tile t) {
        this.boardTiles[x][y] = t;
    }
}
