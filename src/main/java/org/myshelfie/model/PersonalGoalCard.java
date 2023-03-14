package org.myshelfie.model;

public class PersonalGoalCard {
    private Tile[][] constraints;

    public PersonalGoalCard(Tile[][] constraint_tiles) {
        constraints = new Tile[6][5];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                constraints[i][j] = constraint_tiles[i][j];
            }
        }
    }
}
