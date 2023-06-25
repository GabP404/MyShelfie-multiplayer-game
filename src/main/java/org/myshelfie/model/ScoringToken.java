package org.myshelfie.model;

import java.io.Serializable;

/**
 * Class that represents a scoring token.
 */
public class ScoringToken implements Serializable {
    private final Integer points;
    private final String CommonGoalId;

    public ScoringToken(Integer points, String commonGoalId) {
        this.points = points;
        this.CommonGoalId = commonGoalId;
    }

    /**
     * @return The number of points of this token
     */
    public Integer getPoints() {
        return points;
    }

    /**
     * @return The id of the common goal card associated to this token.
     * This information is needed to know which common goal card has already been completed,
     * once the token has been assigned to a player, in order to avoid assigning multiple times.
     */
    public String getCommonGoalId() {
        return CommonGoalId;
    }
}
