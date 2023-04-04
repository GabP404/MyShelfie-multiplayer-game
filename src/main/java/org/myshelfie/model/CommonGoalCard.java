package org.myshelfie.model;

import java.util.ArrayDeque;


public abstract class CommonGoalCard {
    private String id;

    public CommonGoalCard(String id){
        this.id = id;
    }

    public abstract Boolean checkGoalSatisfied(Bookshelf bookshelf);

    public String getId() {
        return id;
    }
}
