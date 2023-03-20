package org.myshelfie.model;
import java.util.List;

public class Game {
    private Player currPlayer;
    private List<Player> players;
    private Board board;
    private List<CommonGoalCard> commonGoals;
    private TileBag tileBag;
    private boolean Playing;

    public Game(List<Player> players, Board board, List<CommonGoalCard> commonGoals, TileBag tileBag) {
        this.players = players;
        this.board = board;
        this.commonGoals = commonGoals;
        this.tileBag = tileBag;
        this.currPlayer = players.get(0);
        suspendGame();
    }

    public Game() {
        suspendGame();
    }

    public void startGame() {
        Playing = true;
    }

    public void suspendGame() {
        Playing = false;
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
        return commonGoals;
    }
    public TileBag getTileBag() {
        return tileBag;
    }
    public Player getNextPlayer() {
        int pos = players.indexOf(currPlayer);
        if( pos == players.size() - 1) return players.get(0);
        return players.get(pos + 1);
    }

    public boolean isPlaying() {
        return Playing;
    }

    public void setCurrPlayer(Player currPlayer) {
        this.currPlayer = currPlayer;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setCommonGoals(List<CommonGoalCard> commonGoals) {
        this.commonGoals = commonGoals;
    }

    public void setTileBag(TileBag tileBag) {
        this.tileBag = tileBag;
    }
}