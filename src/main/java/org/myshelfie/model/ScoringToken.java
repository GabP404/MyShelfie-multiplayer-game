package org.myshelfie.model;

public class ScoringToken {
    private Integer points;
    private String CommonGoalId;

    public ScoringToken(Integer points, String commonGoalId) {
        this.points = points;
        this.CommonGoalId = commonGoalId;
    }
    public Integer getPoints() {
        return points;
    }

    public String getCommonGoalId() {
        return CommonGoalId;
    }
}
