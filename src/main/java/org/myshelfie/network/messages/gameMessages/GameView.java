package org.myshelfie.network.messages.gameMessages;

import org.myshelfie.model.*;
import org.myshelfie.model.util.Pair;

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
    private final HashMap<String,List<ScoringToken>> commonGoalsTokens;
    private final ImmutableBoard board;
    private final Map<String, String> errorState;
    private final ModelState modelState;

    private final List<Pair<ImmutablePlayer,Boolean>> ranking;

    private final String gameName;
    public GameView(Game model) {
        this.currPlayer = new ImmutablePlayer(model.getCurrPlayer());
        this.commonGoals = model.getCommonGoals();
        this.commonGoalsTokens = new HashMap<>();
        model.getCommonGoalsMap().forEach(
                (key,value) -> this.commonGoalsTokens.put(key.getId(), new ArrayList<>(value))
        );
        // this.commonGoals.putAll(model.getCommonGoalsMap()); -> possible error here
        this.players = new ArrayList<>();
        for(Player p: model.getPlayers()) {
            this.players.add(new ImmutablePlayer(p));
        }
        this.gameName = model.getGameName();
        this.board = new ImmutableBoard(model.getBoard());
        this.errorState = new HashMap<>();
        players.forEach( (player) -> errorState.put(player.getNickname(), model.getErrorState(player.getNickname())) );
        this.modelState = model.getModelState();
        List<Pair<Player,Boolean>> temp = model.getRanking();
        this.ranking = new ArrayList<>();
        for(Pair<Player,Boolean> p: temp) {
            this.ranking.add(new Pair<>(new ImmutablePlayer(p.getLeft()),p.getRight()));
        }
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
        return new ArrayList<>(commonGoals);
    }
    public List<ScoringToken> getCommonGoalTokens(String id) {
        return commonGoalsTokens.get(id);
    }
    public ImmutableBoard getBoard() {
        return board;
    }
    public String getGameName() {
        return gameName;
    }
    public ModelState getModelState() {
        return modelState;
    }

    public List<Pair<ImmutablePlayer,Boolean>> getRanking() {
        return ranking;
    }


}
