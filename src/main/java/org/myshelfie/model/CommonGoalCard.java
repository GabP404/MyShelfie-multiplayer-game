package org.myshelfie.model;

public abstract class CommonGoalCard {
    private String id;

    public CommonGoalCard(String id){
        this.id = id;
    }

    public abstract Boolean checkGoalSatisfied(Bookshelf bookshelf) throws WrongArgumentException;

    public String getId() {
        return id;
    }
}
