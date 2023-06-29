package org.myshelfie.model;
import org.myshelfie.model.util.Pair;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.server.Server;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class representing a Game. This is the main class and is considered the model.
 */
public class Game implements Serializable {
    private String gameName;
    private Player currPlayer;
    private List<Player> players;
    private Board board;
    private HashMap<CommonGoalCard,List<ScoringToken>> commonGoals;
    private TileBag tileBag;
    private ModelState modelState;
    // State to resume after reconnection in case the game is paused because there is only one player connected
    private ModelState stateToResume = ModelState.WAITING_SELECTION_TILE;

    /**
     * errorState maps every player nickname to a corresponding (possible) error message
     */
    private Map<String, String> errorState;

    private Player winner;

    private boolean playing;

    /**
     * Empty constructor used to subscribe to the event manager before the game
     * is actually set up.
     */
    public Game() {

    }

    /**
     * Setup method used to initialize the game after its creation.
     * @param players The list of players that will play the game
     * @param board The board of the game
     * @param commonGoals The map associating all the common goal cards to the corresponding stack of scoring tokens
     * @param tileBag The tile bag of the game
     * @param modelState The state of the game
     * @param gameName The name of the game, used in {@link org.myshelfie.controller.LobbyController} to distinguish
     *                 different games and to send the correct messages to the correct game.
     */
    public void setupGame(List<Player> players, Board board, HashMap<CommonGoalCard,List<ScoringToken>> commonGoals, TileBag tileBag, ModelState modelState, String gameName) {
        this.players = players;
        this.board = board;
        this.commonGoals = commonGoals;
        this.tileBag = tileBag;
        this.currPlayer = players.get(0);
        this.modelState = modelState;
        this.errorState = new HashMap<>();
        players.forEach( (player) -> errorState.put(player.getNickname(), null) );
        this.gameName = gameName;
        this.playing = true;
    }

    /**
     * @return The player that is currently playing
     */
    public Player getCurrPlayer() {
        return currPlayer;
    }

    /**
     * @return The list of all the players in the game
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * @return The board of the game
     */
    public Board getBoard() {
        return board;
    }

    /**
    * @return The list of all the common goal cards in the game
     */
    public List<CommonGoalCard> getCommonGoals() {
        List<CommonGoalCard> x = new ArrayList<>();
        commonGoals.forEach(
                (key,value) -> x.add(key)
        );
        return x;
    }

    /**
     * @return The map associating every common goal card to the corresponding stack of scoring tokens
     */
    public HashMap<CommonGoalCard,List<ScoringToken>> getCommonGoalsMap() {
        return commonGoals;
    }

    /**
     * @return The tile bag of this game
     */
    public TileBag getTileBag() {
        return tileBag;
    }

    /**
     * @return The Player which will play next to {@link #currPlayer}
     */
    public Player getNextPlayer() {
        int pos = players.indexOf(currPlayer);
        if( pos == players.size() - 1) return players.get(0);
        return players.get(pos + 1);
    }

    /**
     * @param nickname The nickname of the player to search
     * @return The error string associated to the player with the given nickname
     */
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
            Server.eventManager.notify(GameEvent.ERROR, this);
        }
    }

    /**
     * Reset the error state of all the players. Send the notification only if at least one error state has been reset
     */
    public void resetErrorState() {
        int countReset = 0;
        for (Player p : players) {
            if (errorState.get(p.getNickname()) != null) {
                errorState.put(p.getNickname(), null);
                countReset++;
            }
        }
        if (countReset > 0)
            Server.eventManager.notify(GameEvent.ERROR_STATE_RESET, this);
    }

    /**
     * Removes the top scoring token from the chosen common goal card and returns it.
     * @param c The common goal card from which the token will be removed
     * @return The removed scoring token
     * @throws WrongArgumentException If the common goal card is not found
     */
    public ScoringToken popTopScoringToken(CommonGoalCard c) throws WrongArgumentException {
        LinkedList<ScoringToken> x = (LinkedList<ScoringToken>) commonGoals.get(c);
        if (x == null)
            throw new WrongArgumentException("CommonGoalCard not found");
        try {
            ScoringToken scoringToken = x.removeFirst();
            // notify the server that the token stack has changed
            Server.eventManager.notify(GameEvent.TOKEN_STACK_UPDATE, this);
            return scoringToken;
        } catch (NoSuchElementException e) {
            return null; // no more tokens on this card
        }
    }

    /**
     * Returns the top scoring token from the chosen common goal card, without removing it.
     * @param c The common goal card from which the token will be removed
     * @return The top scoring token
     * @throws WrongArgumentException If the common goal card is not found
     */
    public ScoringToken getTopScoringToken(CommonGoalCard c) throws WrongArgumentException{
        LinkedList<ScoringToken> x = (LinkedList<ScoringToken>) commonGoals.get(c);
        if (x == null)
            throw new WrongArgumentException("CommonGoalCard not found");
        return x.getFirst();
    }

    /**
     * Set the given player to be the current player and notifies an update.
     * @param currPlayer The player that will be set as the current one.
     * @throws WrongArgumentException It the player is not found.
     */
    public void setCurrPlayer(Player currPlayer) throws WrongArgumentException{
        if (currPlayer == null || !players.contains(currPlayer))
            throw new WrongArgumentException("Player not found");
        this.currPlayer.clearSelectedColumn(); // reset the selected column from the previous player
        this.currPlayer = currPlayer; // set the new current player
        Server.eventManager.notify(GameEvent.CURR_PLAYER_UPDATE, this);
    }

    /**
     * @return The current state of this game.
     */
    public ModelState getModelState() {
        return modelState;
    }

    /**
     * Set the current state of this game. Notifies a GAME_END update if the state is END_GAME.
     * @param modelState The state to be set.
     */
    public void setModelState(ModelState modelState) {
        this.modelState = modelState;
        if (modelState == ModelState.END_GAME)
            Server.eventManager.notify(GameEvent.GAME_END, this);
    }

    /**
     * Save the current state of the game, to be resumed after a pause due to too many disconnections.
     */
    public void saveState() {
        this.stateToResume = this.modelState;
    }

    /**
     * Resume the state of the game after some player reconnects.
     */
    public void resumeStateAfterPause() {
        this.modelState = this.stateToResume;
    }

    /**
     * Set the winner of the game.
     * @param winner The player that won the game.
     * @throws WrongArgumentException If the player is not found.
     */
    public void setWinner(Player winner) throws WrongArgumentException {
        if (currPlayer == null || !players.contains(currPlayer))
            throw new WrongArgumentException("Player not found");
        this.modelState = ModelState.END_GAME;
        this.winner = winner;
    }

    /**
     * @return The number of online players
     */
    public int getNumOnlinePlayers() {
        return (int) players.stream().filter(Player::isOnline).count();
    }

    /**
     * @return The name of this game.
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * @return True if the game is playing, false otherwise.
     */
    public boolean isPlaying() {
        return playing;
    }


    /**
     * Get the next player that is online. If there are no online players, return null
     * @return The next online player, or null if there are no online players
     */
    public Player getNextOnlinePlayer() {
        int pos = players.indexOf(currPlayer);
        int count = 0;
        while (count < players.size()) {
            pos = (pos + 1) % players.size();
            if (players.get(pos).isOnline())
                return players.get(pos);
            count++;
        }

        // There are no other online players (maybe only the current)
        if (currPlayer.isOnline())
            return currPlayer;
        return null;
    }


}