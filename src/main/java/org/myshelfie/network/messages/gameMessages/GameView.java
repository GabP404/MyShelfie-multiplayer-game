package org.myshelfie.network.messages.gameMessages;

import org.myshelfie.model.*;
import org.myshelfie.model.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.Serializable;
import java.util.Map;

/**
 * This class is the immutable version of the {@link Game} class, which allows the distributed MVC paradigm to work.
 * In particular this class provides to the {@link org.myshelfie.view.View View} all the information it needs to show the users the game state.
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
    private final String gameName;

    /**
     * Constructor starting from all the information of a {@link Game} object.
     * @param model the {@link Game} object to be converted into an immutable version.
     */
    public GameView(Game model) {
        this.currPlayer = new ImmutablePlayer(model.getCurrPlayer());
        this.commonGoals = model.getCommonGoals();
        this.commonGoalsTokens = new HashMap<>();
        model.getCommonGoalsMap().forEach(
                (key,value) -> this.commonGoalsTokens.put(key.getId(), new ArrayList<>(value))
        );
        this.players = new ArrayList<>();
        for(Player p: model.getPlayers()) {
            this.players.add(new ImmutablePlayer(p));
        }
        this.gameName = model.getGameName();
        this.board = new ImmutableBoard(model.getBoard());
        this.errorState = new HashMap<>();
        players.forEach( (player) -> errorState.put(player.getNickname(), model.getErrorState(player.getNickname())) );
        this.modelState = model.getModelState();
    }

    /**
     * @param nickname Nickname of the player to get the error state of.
     * @return The error string of the player with the given nickname.
     */
    public String getErrorState(String nickname) {
        return errorState.get(nickname);
    }

    /**
     * @return Current player of the game as a {@link ImmutablePlayer} object.
     */
    public ImmutablePlayer getCurrPlayer() {
        return currPlayer;
    }

    /**
     * @return List of players in the game as {@link ImmutablePlayer} objects.
     */
    public List<ImmutablePlayer> getPlayers() {
        return players;
    }

    /**
     * @return List of common goals in the game as {@link CommonGoalCard} objects.
     */
    public List<CommonGoalCard> getCommonGoals() {
        return new ArrayList<>(commonGoals);
    }

    /**
     * @param id Id of the common goal to get the tokens of.
     * @return List of tokens of the common goal with the given id.
     */
    public List<ScoringToken> getCommonGoalTokens(String id) {
        return commonGoalsTokens.get(id);
    }

    /**
     * @return Immutable version of the game board as an {@link ImmutableBoard} object.
     */
    public ImmutableBoard getBoard() {
        return board;
    }

    /**
     * @return Name of the game.
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * @return State of the game as a {@link ModelState} object.
     */
    public ModelState getModelState() {
        return modelState;
    }



}
