package org.myshelfie.network.messages.gameMessages;

import org.myshelfie.model.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class ImmutablePlayer  implements Serializable {
    private final String nickname;
    private final List<ScoringToken> commonGoalTokens;
    private final Boolean hasFinalToken;
    private final PersonalGoalCard personalGoal;
    private final ImmutableBookshelf bookshelf;
    private final List<Tile> tilesPicked;
    private final int selectedColumn;
    private final int bookshelfPoints;
    private final int personalGoalPoints;
    private final int publicPoints;
    private final int totalPoints;
    private final int commonGoalPoints;
    private final boolean online;

    private final boolean winner;
    private static int DIM_TILESPICKED = 3;

    public ImmutablePlayer(Player p) {
        this.nickname = p.getNickname();
        this.commonGoalTokens = new ArrayList<>(p.getCommonGoalTokens());
        this.hasFinalToken = p.getHasFinalToken();
        this.personalGoal = p.getPersonalGoal();
        this.bookshelf = new ImmutableBookshelf(p.getBookshelf());
        this.tilesPicked = new ArrayList<>(p.getTilesPicked());
        this.selectedColumn = p.getSelectedColumn();
        this.bookshelfPoints = p.getBookshelfPoints();
        this.personalGoalPoints = p.getPersonalGoal().getPoints(p.getBookshelf());
        this.publicPoints = p.getPublicPoints();
        this.totalPoints = p.getTotalPoints();
        this.commonGoalPoints = p.getCommonGoalPoints();
        this.online = p.isOnline();
        this.winner = p.isWinner();

    }

    public boolean isWinner() {
        return winner;
    }

    public int getCommonGoalPoints() {
        return commonGoalPoints;
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

    public ImmutableBookshelf getBookshelf() {
        return bookshelf;
    }

    public List<Tile> getTilesPicked() {
        return new ArrayList<>(tilesPicked);
    }

    public int getSelectedColumn() {
        return selectedColumn;
    }

    public int getTotalPoints(){
        return totalPoints;
    }
    public int getPublicPoints() {
        return publicPoints;
    }
    public int getPersonalGoalPoints() {
        return personalGoalPoints;
    }
    public int getBookshelfPoints() {
        return bookshelfPoints;
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

    public boolean isOnline() {
        return online;
    }
}
