package org.myshelfie.model;
import org.myshelfie.network.server.ServerImpl;
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

    public Game(List<Player> players, Board board, HashMap<CommonGoalCard,List<ScoringToken>> commonGoals, TileBag tileBag, ModelState modelState) {
        this.players = players;
        this.board = board;
        this.commonGoals = commonGoals;
        this.tileBag = tileBag;
        this.currPlayer = players.get(0);
        this.modelState = modelState;
        this.winner = null;
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

    public ScoringToken popTopScoringToken(CommonGoalCard c) {
        LinkedList<ScoringToken> x = (LinkedList<ScoringToken>) commonGoals.get(c);
        ScoringToken scoringToken = x.removeFirst();
        // notify the server that the token stack has changed
        ServerImpl.eventManager.notify(GameEvent.TOKEN_UPDATE, null);
        return scoringToken;
    }
    public ScoringToken getTopScoringToken(CommonGoalCard c) {
        LinkedList<ScoringToken> x = (LinkedList<ScoringToken>) commonGoals.get(c);
        return x.getFirst();
    }
    public boolean isPlaying() {
        return playing;
    }

    public void setCurrPlayer(Player currPlayer) {
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

    public void setWinner(Player winner) {
        this.winner = winner;
    }


}