package org.myshelfie.model;

public class ScoringToken {
    private Integer points;
    private String CommonGoalID;

    public ScoringToken(Integer points, String commonGoalID) {
        this.points = points;
        CommonGoalID = commonGoalID;
    }
    public Integer getPoints() {
        return points;
    }

    public String getCommonGoalID() {
        return CommonGoalID;
    }
}
