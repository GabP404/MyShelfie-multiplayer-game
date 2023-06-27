package org.myshelfie.model;

import org.myshelfie.controller.Configuration;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.server.Server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class that represents a player in the game.
 */
public class Player implements Serializable {
    private final String nickname;
    private List<ScoringToken> commonGoalTokens;
    private Boolean hasFinalToken;
    private PersonalGoalCard personalGoal;
    private final Bookshelf bookshelf;
    private List<LocatedTile> tilesPicked;
    private int selectedColumn;

    private boolean winner;
    private boolean online;

    private static final int DIM_TILESPICKED = 3;

    /**
     * Constructor of the Player class.
     * @param nick      The player's nickname
     * @param persGoal  The player's personal goal card
     */
    public Player(String nick, PersonalGoalCard persGoal) {
        this.nickname = nick;
        this.commonGoalTokens = new ArrayList<>();
        this.hasFinalToken = false;
        this.personalGoal = persGoal;
        this.bookshelf = new Bookshelf();
        this.tilesPicked = new ArrayList<>();
        this.selectedColumn = -1;
        this.online = true;
        this.winner = false;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public String getNickname() {
        return nickname;
    }

    public Boolean getHasFinalToken() {
        return hasFinalToken;
    }

    /**
     * Assign the final token to this palyer and notify un update.
     * @param hasFinalToken True if the player has the final token, false otherwise
     */
    public void setHasFinalToken(Boolean hasFinalToken) {
        this.hasFinalToken = hasFinalToken;
        // notify the server that the final token has changed
        Server.eventManager.notify(GameEvent.FINAL_TOKEN_UPDATE, this);
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
     * Assign a scorking token to this player.
     * @param t The token
     */
    public void addScoringToken(ScoringToken t) {
        if (t == null) return;
        commonGoalTokens.add(t);
        Server.eventManager.notify(GameEvent.TOKEN_UPDATE, this);
    }

    /**
     * @return the list of the common goal tokens.
     */
    public List<ScoringToken> getCommonGoalTokens() {
        return commonGoalTokens;
    }

    public List<LocatedTile> getTilesPicked() {
        return tilesPicked;
    }

    /**
     * Setter for the hand of tiles picked by the player.
     * @param tilesPicked  The list of tiles picked
     */
    public void setTilesPicked(List<LocatedTile> tilesPicked) {
        this.tilesPicked = new ArrayList<>(tilesPicked);
    }

    /**
     * Getter for a single tile in the hand of this player.
     * This method is used in {@link org.myshelfie.controller.SelectTileFromHandCommand#execute}.
     * @param index The index of the tile inside the hand
     * @return The located tile in the position index inside the player's hand
     * @throws WrongArgumentException If the index is out of bound
     */
    public LocatedTile getTilePicked(int index) throws WrongArgumentException {
        if(index < 0 || index > this.tilesPicked.size()) throw new WrongArgumentException("Tile's index out of bound");
        return this.tilesPicked.get(index);
    }

    /**
     * Add a tile to the hand of this player.
     * @param t The located tile to add
     * @throws WrongArgumentException If the hand is full
     */
    public void addTilesPicked(LocatedTile t) throws WrongArgumentException{
        if(this.tilesPicked.size() == DIM_TILESPICKED) throw new WrongArgumentException("Maximum number of tiles picked reached");
        this.tilesPicked.add(t);
    }

    /**
     * Getter for the public points, i.e. the points earned from the common goal tokens
     * or from groups of adjacent tiles of the same type in the bookshelf.
     * @return number of public points
     */
    public int getPublicPoints() {
        int points_scoringToken = 0;
        for (ScoringToken s : this.commonGoalTokens) {
            points_scoringToken += s.getPoints();
        }
        int points_group = getBookshelfPoints();
        return points_scoringToken + points_group;
    }

    public int getCommonGoalPoints() {
        int points_scoringToken = 0;
        for (ScoringToken s : this.commonGoalTokens) {
            points_scoringToken += s.getPoints();
        }
        return points_scoringToken;
    }

    /**
     * Getter for the points earned from groups of adjacent tiles of the same type.
     * @return number of private points
     */
    public int getBookshelfPoints() {
        HashMap<Integer,Integer> mapping = Configuration.getMapPointsGroup();
        int points_group = 0;
        List<Integer> groups = this.bookshelf.getAdjacentSizes();
        int maxKey = Integer.MIN_VALUE;

        for (int key : mapping.keySet()) {
            if (key > maxKey) {
                maxKey = key;
            }
        }
        for (Integer g :
                groups) {
            if(g > maxKey) points_group += mapping.get(maxKey);
            else points_group += mapping.get(g);
        }
        return points_group;
    }

    /**
     * Remove the tile t from the hand of this player.
     * @param t The tile to remove
     * @throws WrongArgumentException If the tile is not in the hand
     */
    public void removeTilesPicked(LocatedTile t) throws WrongArgumentException{
        if (!this.tilesPicked.contains(t)) throw new WrongArgumentException("Tile not found");
        this.tilesPicked.remove(t);
        Server.eventManager.notify(GameEvent.TILES_PICKED_UPDATE, this);
    }

    public int getSelectedColumn() {
        return selectedColumn;
    }

    /**
     * Setter for the selected column. If the set goes well, notify an update.
     * @param selectedColumn The index of the selected column
     * @throws WrongArgumentException If the index is out of bound
     */
    public void setSelectedColumn(int selectedColumn) throws WrongArgumentException {
        if(selectedColumn < 0 || selectedColumn >= Bookshelf.NUMCOLUMNS) {
            throw new WrongArgumentException("Column Out of range");
        }
        this.selectedColumn = selectedColumn;
        Server.eventManager.notify(GameEvent.SELECTED_COLUMN_UPDATE, this);
    }

    /**
     * Clears the hand of picked tiles for this player.
     */
    public void clearHand() {
        this.tilesPicked.clear();
    }

    /**
     * Clears the selected column for this player.
     */
    public void clearSelectedColumn() {
        this.selectedColumn = -1;
    }

    /**
     * @return True if this player is online, false if not.
     */
    public boolean isOnline() {
        return online;
    }

    /**
     * Sets the player's online status and notifies an update.
     * @param online The online/offline status to set for this player.
     */
    public void setOnline(boolean online) {
        this.online = online;
        Server.eventManager.notify(GameEvent.PLAYER_ONLINE_UPDATE, this);
    }

    /**
     * Method to retrieve the overall score for this player.
     * @return The overall score
     */
    public int getTotalPoints(){
        return getPublicPoints() + this.personalGoal.getPoints(this.bookshelf) +  (this.hasFinalToken ? 1 : 0);
    }
}
