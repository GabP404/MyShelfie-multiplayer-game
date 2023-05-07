package org.myshelfie.network.messages.gameMessages;

import org.myshelfie.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.Serializable;
import java.util.Map;

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
    private final Map<String, String> errorState;
    private final ModelState modelState;
    public GameView(Game model) {
        this.currPlayer = new ImmutablePlayer(model.getCurrPlayer());
        this.commonGoals = model.getCommonGoals();
        this.players = new ArrayList<>();
        for(Player p: model.getPlayers()) {
            this.players.add(new ImmutablePlayer(p));
        }
        this.board = new ImmutableBoard(model.getBoard());
        this.errorState = new HashMap<>();
        players.forEach( (player) -> errorState.put(player.getNickname(), model.getErrorState(player.getNickname())) );
        this.modelState = model.getModelState();
    }

    public String getErrorState(String nickname) {
        return errorState.get(nickname);
    }

    public ModelState getModelState() {
        return modelState;
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
