package org.myshelfie.model;
import org.myshelfie.network.server.Server;
import org.myshelfie.network.messages.gameMessages.GameEvent;

import java.util.*;

public class Game {
    private Player currPlayer;
    private List<Player> players;
    private Board board;
    private HashMap<CommonGoalCard,List<ScoringToken>> commonGoals;
    private TileBag tileBag;
    private boolean playing;

    private ModelState modelState;

    private Player winner;
    private Map<String, Boolean> errorState;

    public Game(List<Player> players, Board board, HashMap<CommonGoalCard,List<ScoringToken>> commonGoals, TileBag tileBag, ModelState modelState) {
        this.players = players;
        this.board = board;
        this.commonGoals = commonGoals;
        this.tileBag = tileBag;
        this.currPlayer = players.get(0);
        this.modelState = modelState;
        this.winner = null;
        this.errorState = new HashMap<>();
        players.forEach( (player) -> errorState.put(player.getNickname(), false) );
        try {
            this.board.refillBoard(this.players.size(), tileBag);
        } catch (WrongArgumentException e) {
            throw new RuntimeException(e);
        }
        suspendGame();
    }

    public Game() {
        suspendGame();
    }

    public void startGame() {
        playing = true;
    }

    public void suspendGame() {
        playing = false;
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

    public Boolean getErrorState(String nickname) {
        Boolean res = errorState.get(nickname);
        if (res == null)
            return false;
        return res;
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
            this.errorState.put(nickname, true);
            Server.eventManager.notify(GameEvent.ERROR, errorMessage);
        }
    }

    /**
     * Reset the error state of all the players
     */
    public void resetErrorState() {
        players.forEach( (player) -> errorState.put(player.getNickname(), false) );
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
    public boolean isPlaying() {
        return playing;
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


}