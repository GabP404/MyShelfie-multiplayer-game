package org.myshelfie.model;

public class PersonalGoalCard {
    private List<Pair<Pair<int, int>, Tile>> constraints;

    public PersonalGoalCard(List<Pair<Pair<int, int>, Tile>> constraint_tiles) {
        constraints = constraint_tiles;
    }
}
