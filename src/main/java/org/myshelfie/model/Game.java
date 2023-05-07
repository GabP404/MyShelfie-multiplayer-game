package org.myshelfie.model;
import org.myshelfie.network.server.Server;
import org.myshelfie.network.messages.gameMessages.GameEvent;

import java.io.Serializable;
import java.util.*;

public class Game {

    private String gameName;;
    private Player currPlayer;
    private List<Player> players;
    private Board board;
    private HashMap<CommonGoalCard,List<ScoringToken>> commonGoals;
    private TileBag tileBag;

    private ModelState modelState;

    private Player winner;
    /**
     * errorState maps every player nickname to a corresponding (possible) error message
     */
    private Map<String, String> errorState;

    private boolean playing;
    public Game() {

    }

    public void setupGame(List<Player> players, Board board, HashMap<CommonGoalCard,List<ScoringToken>> commonGoals, TileBag tileBag, ModelState modelState, String gameName) {
        this.players = players;
        this.board = board;
        this.commonGoals = commonGoals;
        this.tileBag = tileBag;
        this.currPlayer = players.get(0);
        this.modelState = modelState;
        this.winner = null;
        this.errorState = new HashMap<>();
        players.forEach( (player) -> errorState.put(player.getNickname(), null) );

        this.gameName = gameName;
        this.playing = true;
    }


    public Player getCurrPlayer() {
        return currPlayer;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Board getBoard() {
        return board;
    }

    public List<CommonGoalCard> getCommonGoals() {
        List<CommonGoalCard> x = new ArrayList<>();
        commonGoals.forEach(
                (key,value) -> x.add(key)
        );
        return x;
    }
    public TileBag getTileBag() {
        return tileBag;
    }
    public Player getNextPlayer() {
        int pos = players.indexOf(currPlayer);
        if( pos == players.size() - 1) return players.get(0);
        return players.get(pos + 1);
    }

    public String getErrorState(String nickname) {
        return errorState.get(nickname);
    }

    /**
     * Set the error state for a player and notify the server adding the error message
     * @param nickname The nickname of the player
     * @param errorMessage The error message that will be incapsulated in the message
     */
    public void setErrorState(String nickname, String errorMessage) {
        resetErrorState();
        // if the nickname belongs to one of the players, set the error state to true
        if (players.stream().anyMatch( (player) -> player.getNickname().equals(nickname) )) {
            this.errorState.put(nickname, errorMessage);
            Server.eventManager.notify(GameEvent.ERROR);
        }
    }

    /**
     * Reset the error state of all the players
     */
    public void resetErrorState() {
        players.forEach( (player) -> errorState.put(player.getNickname(), null) );
    }

    public ScoringToken popTopScoringToken(CommonGoalCard c) throws WrongArgumentException {
        LinkedList<ScoringToken> x = (LinkedList<ScoringToken>) commonGoals.get(c);
        if (x == null)
            throw new WrongArgumentException("CommonGoalCard not found");
        ScoringToken scoringToken = x.removeFirst();
        // notify the server that the token stack has changed
        Server.eventManager.notify(GameEvent.TOKEN_UPDATE);
        return scoringToken;
    }
    public ScoringToken getTopScoringToken(CommonGoalCard c) throws WrongArgumentException{
        LinkedList<ScoringToken> x = (LinkedList<ScoringToken>) commonGoals.get(c);
        if (x == null)
            throw new WrongArgumentException("CommonGoalCard not found");
        return x.getFirst();
    }

    public void setCurrPlayer(Player currPlayer) throws WrongArgumentException{
        if (currPlayer == null || !players.contains(currPlayer))
            throw new WrongArgumentException("Player not found");
        this.currPlayer = currPlayer;
    }

    public ModelState getModelState() {
        return modelState;
    }

    public void setModelState(ModelState modelState) {
        this.modelState = modelState;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) throws WrongArgumentException {
        if (currPlayer == null || !players.contains(currPlayer))
            throw new WrongArgumentException("Player not found");
        this.winner = winner;
    }

    public String getGameName() {
        return gameName;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        // TODO: to handle player's disconnection a notify with a specific event will be required
        //  (also for the setter of Player's online attribute)
        this.playing = playing;
    }
}