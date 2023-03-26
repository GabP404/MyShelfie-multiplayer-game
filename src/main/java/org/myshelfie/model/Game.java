package org.myshelfie.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.myshelfie.model.CommonGoalDeck.COMMON_GOAL_DECK_SIZE;

public class Game {
    private Player currPlayer;
    private List<Player> players;
    private Board board;
    private List<CommonGoalCard> commonGoals;
    private TileBag tileBag;
    private boolean playing;

    public Game(List<String> nickNames, Board board, TileBag tileBag) {
        this.board = board;
        this.tileBag = tileBag;
        this.currPlayer = players.get(0);
        suspendGame();
        createPlayers(nickNames);
        CommonGoalDeck commonGoalDeck = new CommonGoalDeck(players.size());
        this.commonGoals = commonGoalDeck.drawCommonGoalCard();
    }

    /**
     * Create a personalGoalDeck in order to get the PersonalGoalCard for the players.
     * Create the players
     * @param nickNames
     */
    private void createPlayers(List<String> nickNames) {
        PersonalGoalDeck personalGoalDeck = new PersonalGoalDeck();
        List<PersonalGoalCard> drawnPersonalGoalCard = personalGoalDeck.draw(players.size());
        this.players = new ArrayList<>();
        for(int i = 0; i < players.size(); i++) {
            players.add(new Player(nickNames.get(i),drawnPersonalGoalCard.get(i)));
        }
    }

    public Game() {
        suspendGame();
    }

    public void startGame() {
        playing = true;
    }

    public void suspendGame() {
        playing = false;
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
        return playing;
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

    public void setTileBag(TileBag tileBag) {
        this.tileBag = tileBag;
    }
}