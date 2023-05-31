package org.myshelfie.model;

import java.io.Serializable;

public abstract class CommonGoalCard implements Serializable {
    private String id;

    public CommonGoalCard(String id){
        this.id = id;
    }

    public abstract Boolean checkGoalSatisfied(Bookshelf bookshelf) throws WrongArgumentException;

    public String getId() {
        return id;
    }
}
