package org.myshelfie.model;

public class PersonalGoalCard {
    private List<Pair<Pair<Integer, Integer>, Tile>> constraints;

    public PersonalGoalCard(List<Pair<Pair<Integer, Integer>, Tile>> constraint_tiles) {
        constraints = constraint_tiles;
    }
}
