package org.myshelfie.model;

public class Board {
    private Tile[][] boardTiles;

    public Board() {
        this.boardTiles = new Tile[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.boardTiles = null;
            }
        }
    }

    public void setTile(int x, int y, Tile t) {
        this.boardTiles[x][y] = t;
    }
}
