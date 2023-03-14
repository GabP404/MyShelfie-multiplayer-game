package org.myshelfie.model;

final class PersonalGoalCard {
    private Tile[][] constraints;

    public PersonalGoalCard() {
        constraints = new Tile[6][5];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                constraints[i][j] = null;
            }
        }
    }

    public void setTile(int col, int row, ItemType type) throws TileInsertionException {
        if (col < 0 || row < 0 || col > 5 || row > 6)
            throw new TileInsertionException("Coordinates for the tile not valid!");

        constraints[row][col] = new Tile(); //TODO use constructor to specify ItemType
    }
}
