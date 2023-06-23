package org.myshelfie.model;
import org.myshelfie.model.util.Pair;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.server.Server;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Game implements Serializable {

    private String gameName;
    private Player currPlayer;
    private List<Player> players;
    private Board board;
    private HashMap<CommonGoalCard,List<ScoringToken>> commonGoals;
    private TileBag tileBag;

    private ModelState modelState;

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
        ScoringToken scoringToken = x.removeFirst();
        // notify the server that the token stack has changed
        Server.eventManager.notify(GameEvent.TOKEN_STACK_UPDATE, this);
        return scoringToken;
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

    public int getNumOnlinePlayers() {
        return (int) players.stream().filter(Player::isOnline).count();
    }

    public String getGameName() {
        return gameName;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        // TODO: to handle player's disconnection a notify with a specific event will be required
        //  (also for the setter of Player's online attribute)
        this.playing = playing;
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
        return null;
    }

    /**
     * Return a list of pairs (player, isWinner) ordered by score descending
     * @return
     */
    public List<Pair<Player,Boolean>> getRanking(){
        List<Player> playersSortedByScore = players.stream()
                .sorted(Comparator.comparing(Player::getTotalPoints).reversed()).toList();

        List<Player> playersOnline = players.stream().filter(Player::isOnline).collect(Collectors.toList());
        playersOnline.sort(Comparator.comparing(Player::getTotalPoints).reversed());
        int maxPoints = playersOnline.get(0).getTotalPoints();
        List<Player> playersWithMaxPoints = playersOnline.stream()
                .filter(player -> player.getTotalPoints() == maxPoints).toList();
        List<Pair<Player,Boolean>> ranking = new ArrayList<>();
        playersWithMaxPoints.forEach(player -> System.out.println(player.getNickname() + " " + player.getTotalPoints()));
        for (Player p : playersSortedByScore) {
            if (playersWithMaxPoints.contains(p))
                ranking.add(new Pair<>(p, true));
            else
                ranking.add(new Pair<>(p, false));
        }
        ranking.stream().forEach(x -> System.out.println(x.getLeft().getNickname() + " "+ x.getRight()));
        return ranking;
    }
}