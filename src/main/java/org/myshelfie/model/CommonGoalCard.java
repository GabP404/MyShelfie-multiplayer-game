package org.myshelfie.model;

import java.io.Serializable;

/**
 * CommonGoalCard abstract class that allows to implement a strategy pattern.
 */
public abstract class CommonGoalCard implements Serializable {
    private final String id;

    public CommonGoalCard(String id){
        this.id = id;
    }

    /**
     * Check if the goal is satisfied.
     * @param bookshelf the bookshelf to check
     * @return true if the goal is satisfied, false otherwise
     * @throws WrongArgumentException when trying to access a tile outside the bookshelf
     */
    public abstract Boolean checkGoalSatisfied(Bookshelf bookshelf) throws WrongArgumentException;

    /**
     * Get the id of the common goal card.
     * @return the id of the common goal card
     */
    public String getId() {
        return id;
    }
}
