package org.myshelfie.network.messages.gameMessages;

import org.myshelfie.model.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Immutable version of {@link Player} to be sent to the client.
 */
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
    private static final int DIM_TILESPICKED = 3;

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

    /**
     * @return The player's nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @return The list of common goal tokens owned by the player.
     */
    public List<ScoringToken> getCommonGoalTokens() {
        return new ArrayList<>(commonGoalTokens);
    }

    /**
     * @return True if the player has the final token, false otherwise
     */
    public Boolean getHasFinalToken() {
        return hasFinalToken;
    }

    /**
     * @return The personal goal card of the player
     */
    public PersonalGoalCard getPersonalGoal() {
        return personalGoal;
    }

    /**
     * @return The player's bookshelf as a {@link ImmutableBookshelf} object
     */
    public ImmutableBookshelf getBookshelf() {
        return bookshelf;
    }

    /**
     * @return The list of tiles in the hand of this player
     */
    public List<Tile> getTilesPicked() {
        return new ArrayList<>(tilesPicked);
    }

    /**
     * @return The column selected by the player for tiles' insertion
     */
    public int getSelectedColumn() {
        return selectedColumn;
    }

    /**
     * @return Total number of points earnt by the player
     */
    public int getTotalPoints(){
        return totalPoints;
    }

    /**
     * Getter for the public points, i.e. the points earned from the common goal tokens
     * or from groups of adjacent tiles of the same type in the bookshelf.
     * @return number of public points
     */
    public int getPublicPoints() {
        return publicPoints;
    }

    /**
     * @return Number of points earnt from the personal goal card
     */
    public int getPersonalGoalPoints() {
        return personalGoalPoints;
    }

    /**
     * @return Number of points earnt from groups of adjacent tiles of the same type
     */
    public int getBookshelfPoints() {
        return bookshelfPoints;
    }

    /**
     * @return number of points earnt from the common goal tokens
     */
    public int getPointsScoringTokens() {
        int x = 0;
        for (ScoringToken s :
                this.commonGoalTokens) {
            x+= s.getPoints();
        }
        return x;
    }

    /**
     * @return The online/offline status of this player
     */
    public boolean isOnline() {
        return online;
    }
}
