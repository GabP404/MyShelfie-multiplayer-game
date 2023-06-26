package org.myshelfie.model;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.server.Server;

import java.io.Serializable;
import java.util.*;

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

    private Player winner;
    /**
     * errorState maps every player nickname to a corresponding (possible) error message
     */
    private Map<String, String> errorState;

    private boolean playing;
    public Game() {

    }

    public void setupGame(List<Player> players, Board board, HashMap<CommonGoalCard,List<ScoringToken>> commonGoals, TileBag tileBag, ModelState modelState, String gameName) {
        this.players = players;
        this.board = board;
        this.commonGoals = commonGoals;
        this.tileBag = tileBag;
        this.currPlayer = players.get(0);
        this.modelState = modelState;
        this.winner = null;
        this.errorState = new HashMap<>();
        players.forEach( (player) -> errorState.put(player.getNickname(), null) );

        this.gameName = gameName;
        this.playing = true;
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
    public HashMap<CommonGoalCard,List<ScoringToken>> getCommonGoalsMap() {
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

    public ScoringToken getTopScoringToken(CommonGoalCard c) throws WrongArgumentException{
        LinkedList<ScoringToken> x = (LinkedList<ScoringToken>) commonGoals.get(c);
        if (x == null)
            throw new WrongArgumentException("CommonGoalCard not found");
        return x.getFirst();
    }

    public void setCurrPlayer(Player currPlayer) throws WrongArgumentException{
        if (currPlayer == null || !players.contains(currPlayer))
            throw new WrongArgumentException("Player not found");
        this.currPlayer.clearSelectedColumn(); // reset the selected column from the previous player
        this.currPlayer = currPlayer; // set the new current player
        Server.eventManager.notify(GameEvent.CURR_PLAYER_UPDATE, this);
    }

    public ModelState getModelState() {
        return modelState;
    }

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

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) throws WrongArgumentException {
        if (winner == null || !players.contains(winner))
            throw new WrongArgumentException("Player not found");
        this.modelState = ModelState.END_GAME;
        this.winner = winner;
    }

    public int getNumOnlinePlayers() {
        return (int) players.stream().filter(Player::isOnline).count();
    }

    public String getGameName() {
        return gameName;
    }

    public boolean isPlaying() {
        return playing;
    }

    /**
     * Get the next player that is online. If there are no online players, return null
     * @return
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