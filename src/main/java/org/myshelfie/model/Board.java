package org.myshelfie.model;

public class Board {
    private static final int DIMBOARD = 9;
    private Tile[][] boardTiles;

    public Board() {
        this.boardTiles = new Tile[DIMBOARD][DIMBOARD];
    }

    public void setTile(int x, int y, Tile t) {
        this.boardTiles[x][y] = t;
    }
}
