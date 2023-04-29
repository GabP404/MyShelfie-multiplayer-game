package org.myshelfie.network.messages.gameMessages;

import org.myshelfie.model.CommonGoalCard;
import org.myshelfie.model.Game;
import org.myshelfie.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

/**
 * This class is the immutable version of the Game (model) class, which allows the distributed MVC paradigm to work.
 * In particular this class provides to the View all the information it needs to show the users the game state.
 */
public class GameView implements Serializable {
    static final long serialVersionUID = 1L;
    private final ImmutablePlayer currPlayer;
    private final List<ImmutablePlayer> players;
    private final List<CommonGoalCard> commonGoals;
    private final ImmutableBoard board;
    public GameView(Game model) {
        this.currPlayer = new ImmutablePlayer(model.getCurrPlayer());
        this.commonGoals = model.getCommonGoals();
        this.players = new ArrayList<>();
        for(Player p: model.getPlayers())
        {
            this.players.add(new ImmutablePlayer(p));
        }
        this.board = new ImmutableBoard(model.getBoard());
    }

    public ImmutablePlayer getCurrPlayer() {
        return currPlayer;
    }

    public List<ImmutablePlayer> getPlayers() {
        return players;
    }

    public List<CommonGoalCard> getCommonGoals() {
        return commonGoals;
    }

    public ImmutableBoard getBoard() {
        return board;
    }
}