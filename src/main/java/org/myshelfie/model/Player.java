package org.myshelfie.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String nickname;
    private List<ScoringToken> commonGoalTokens;
    private Boolean hasFinalToken;
    private PersonalGoalCard personalGoal;
    private Bookshelf bookshelf;

    /**
     * Constructor of the Player class.
     * @param nick      The player's nickname
     * @param persGoal  The player's personal goal card
     */
    public Player(String nick, PersonalGoalCard persGoal) {
        this.nickname = new String(nick);
        this.commonGoalTokens = new ArrayList<ScoringToken>();
        this.hasFinalToken = false;
        this.personalGoal = persGoal;
        this.bookshelf = new Bookshelf();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Boolean getHasFinalToken() {
        return hasFinalToken;
    }

    public void setHasFinalToken(Boolean hasFinalToken) {
        this.hasFinalToken = hasFinalToken;
    }

    public PersonalGoalCard getPersonalGoal() {
        return personalGoal;
    }

    public void setPersonalGoal(PersonalGoalCard personalGoal) {
        this.personalGoal = personalGoal;
    }

    public Bookshelf getBookshelf() {
        return bookshelf;
    }

    /**
     * Assign a token to this player.
     * @param t The token
     */
    public void addScoringToken(ScoringToken t) {
        this.commonGoalTokens.add(t);
    }

    /**
     * @return the list of the common goal tokens.
     */
    public List<ScoringToken> getCommonGoalTokens() {
        return commonGoalTokens;
    }
}
