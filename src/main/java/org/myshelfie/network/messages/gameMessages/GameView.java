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
    //private final List<CommonGoalCard> commonGoals;
    private HashMap<CommonGoalCard,List<ScoringToken>> commonGoals;
    private final ImmutableBoard board;
    private final Map<String, String> errorState;

    private final String gameName;
    public GameView(Game model) {
        this.currPlayer = new ImmutablePlayer(model.getCurrPlayer());


        //copy hashmap in model common goals to this.commonGoals
        this.commonGoals = new HashMap<>();
        //for each element in model.commonGoals, add it to this.commonGoals
        this.commonGoals.putAll(model.getCommonGoalsMap());

        this.players = new ArrayList<>();
        for(Player p: model.getPlayers()) {
            this.players.add(new ImmutablePlayer(p));
        }
        this.gameName = model.getGameName();
        this.board = new ImmutableBoard(model.getBoard());
        this.errorState = new HashMap<>();
        players.forEach( (player) -> errorState.put(player.getNickname(), model.getErrorState(player.getNickname())) );
    }

    public String getErrorState(String nickname) {
        return errorState.get(nickname);
    }

    public ImmutablePlayer getCurrPlayer() {
        return currPlayer;
    }

    public List<ImmutablePlayer> getPlayers() {
        return players;
    }

    public List<CommonGoalCard> getCommonGoals() {
        List<CommonGoalCard> x = new ArrayList<>();
        commonGoals.forEach(
                (key,value) -> x.add(key)
        );
        return x;
    }

    public HashMap<CommonGoalCard,List<ScoringToken>> getCommonGoalsMap() {
        return commonGoals;
    }

    public ImmutableBoard getBoard() {
        return board;
    }

    public String getGameName() {
        return gameName;
    }
}
