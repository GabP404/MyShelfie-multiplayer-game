package org.myshelfie.model;

import java.util.List;
import java.io.Serializable;

/**
 * This class is the immutable version of the Game (model) class, which allows the distributed MVC paradigm to work.
 * In particular this class provides to the View all the information it needs to show the users the game state.
 */
public class GameView implements Serializable {
    // TODO: understand if it's correct for this class to be mutable
    static final long serialVersionUID = 1L;
    private final Player currPlayer;
    private final List<Player> players;
    private final List<CommonGoalCard> commonGoals;
    private final Board board;

    // TODO: add suspended game handling (also in the view)
    // private boolean playing;

    public GameView(Game model) {
        this.currPlayer = model.getCurrPlayer();
        this.commonGoals = model.getCommonGoals();
        this.players = model.getPlayers();
        this.board = model.getBoard();
    }

    public Player getCurrPlayer() {
        return currPlayer;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<CommonGoalCard> getCommonGoals() {
        return commonGoals;
    }

    public Board getBoard() {
        return board;
    }
}
