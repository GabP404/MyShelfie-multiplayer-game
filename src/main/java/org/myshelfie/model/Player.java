package org.myshelfie.model;

import org.myshelfie.network.server.ServerImpl;
import org.myshelfie.network.messages.gameMessages.GameEvent;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String nickname;
    private List<ScoringToken> commonGoalTokens;
    private Boolean hasFinalToken;
    private PersonalGoalCard personalGoal;
    private Bookshelf bookshelf;
    private List<Tile> tilesPicked;
    private int selectedColumn;

    private static int DIM_TILESPICKED = 3;

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
        this.tilesPicked = new ArrayList<Tile>();
        this.selectedColumn = -1;
    }

    public String getNickname() {
        return nickname;
    }

    public Boolean getHasFinalToken() {
        return hasFinalToken;
    }

    public void setHasFinalToken(Boolean hasFinalToken) {
        this.hasFinalToken = hasFinalToken;
        // notify the server that the final token has changed
        ServerImpl.eventManager.notify(GameEvent.TOKEN_UPDATE,  null);
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

    public List<Tile> getTilesPicked() {
        return tilesPicked;
    }
    public void setTilesPicked(List<Tile> tilesPicked) {
        this.tilesPicked = tilesPicked;
    }

    public void addTilesPicked(Tile t) throws TileInsertionException{
        if(this.tilesPicked.size() == DIM_TILESPICKED) throw new TileInsertionException("maximum number of tiles picked reached");
        this.tilesPicked.add(t);
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

    public void removeTilesPicked(Tile t){
        this.tilesPicked.remove(t);
    }

    public void removeTilesPicked(List<Tile> tilesRemoved) {
        for(Tile t: tilesRemoved) {
            this.tilesPicked.remove(t);
        }
    }

    public int getSelectedColumn() {
        return selectedColumn;
    }

    public void setSelectedColumn(int selectedColumn) {
        this.selectedColumn = selectedColumn;
    }
}
