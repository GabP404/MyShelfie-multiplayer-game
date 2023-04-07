package org.myshelfie.model;

import java.util.ArrayList;
import java.util.List;

public final class ImmutablePlayer {
    private final String nickname;
    private final List<ScoringToken> commonGoalTokens;
    private final Boolean hasFinalToken;
    private final PersonalGoalCard personalGoal;
    private final ImmutableBookshelf immutableBookshelf;

    private final List<Tile> tilesPicked;
    private static int DIM_TILESPICKED = 3;

    public ImmutablePlayer(Player p) {
        this.nickname = p.getNickname();
        this.commonGoalTokens = new ArrayList<>(p.getCommonGoalTokens());
        this.hasFinalToken = p.getHasFinalToken();
        this.personalGoal = p.getPersonalGoal();
        this.immutableBookshelf = new ImmutableBookshelf(p.getBookshelf());
        this.tilesPicked = new ArrayList<>(p.getTilesPicked());
    }

    public String getNickname() {
        return nickname;
    }

    public List<ScoringToken> getCommonGoalTokens() {
        return new ArrayList<>(commonGoalTokens);
    }

    public Boolean getHasFinalToken() {
        return hasFinalToken;
    }

    public PersonalGoalCard getPersonalGoal() {
        return personalGoal;
    }

    public ImmutableBookshelf getImmutableBookshelf() {
        return immutableBookshelf;
    }

    public List<Tile> getTilesPicked() {
        return new ArrayList<>(tilesPicked);
    }


    /**
     * @return number of points earnt from ScoringTokens
     */
    public int getPointsScoringTokens() {
        int x = 0;
        for (ScoringToken s :
                this.commonGoalTokens) {
            x+= s.getPoints();
        }
        return x;
    }
}
