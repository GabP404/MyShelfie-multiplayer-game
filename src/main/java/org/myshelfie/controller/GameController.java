package org.myshelfie.controller;

import org.myshelfie.model.*;
import org.myshelfie.network.messages.commandMessages.*;

import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.server.Server;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class GameController implements Serializable {

    public static class GameDefinition implements Serializable {
        private final String gameName;
        private final int maxPlayers;
        private final List<String> nicknames;

        private final boolean simplifyRules;



        public GameDefinition(GameController gc) {
            this.gameName = gc.getGameName();
            this.maxPlayers = gc.getNumPlayerGame();
            this.nicknames = new ArrayList<>(gc.getNicknames());
            this.simplifyRules = gc.getNumGoalCards() == 1;
        }

        public String getGameName() {
            return gameName;
        }

        public int getMaxPlayers() {
            return maxPlayers;
        }

        public List<String> getNicknames() {
            return nicknames;
        }

        public boolean isSimplifyRules() {
            return simplifyRules;
        }

        public boolean isFull() {
            return nicknames.size() == maxPlayers;
        }
    }
    private transient Timer timer; // declared as transient to not serialize it

    private int timeout;
    private boolean isRunning;

    // declared as transient to not serialize it
    private transient ExecutorService commandExecutor;

    public void startTimer() {
        timer = new Timer();
        timer.schedule(new GameTimerTask(), this.timeout); // 1 minute = 60,000 milliseconds
        isRunning = true;
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            isRunning = false;
        }
    }

    public boolean isTimerRunning() {
        return isRunning;
    }

    private class GameTimerTask extends TimerTask {
        @Override
        public void run() {
            endGame();
            try {
                getGame().setWinner(getGame().getPlayers().stream().filter(Player::isOnline).toList().get(0));
                Server.eventManager.notify(GameEvent.GAME_END, getGame());
                Server.eventManager.sendToClients();
            } catch (WrongArgumentException e) {
                throw new RuntimeException(e);
            } catch (IndexOutOfBoundsException e) {
                // All the players are offline (get(0) went out of bound), the game ends!
            }
            isRunning = false;
        }
    }


    private String gameName;

    private Game game;
    private List<String> nicknames;

    private int numPlayerGame;

    public int getNumGoalCards() {
        return numGoalCards;
    }

    private int numGoalCards;

    public GameController(String gameName, int numPlayerGame, int numGoalCards) {
        this.gameName = gameName;
        this.nicknames = new ArrayList<>();
        this.numPlayerGame = numPlayerGame;
        this.numGoalCards = numGoalCards;
        this.timeout = Configuration.getTimerTimeout();
        this.game = new Game();
        createCommandExecutor();
    }

    //creates a new command executor
    public void createCommandExecutor() {
        this.commandExecutor = Executors.newSingleThreadExecutor();
    }

    public void setupGame() throws IOException, URISyntaxException {
        CommonGoalDeck commonGoalDeck = CommonGoalDeck.getInstance();
        PersonalGoalDeck personalGoalDeck = PersonalGoalDeck.getInstance();
        List<PersonalGoalCard> personalGoalCardsGame = personalGoalDeck.draw(numPlayerGame);
        List<CommonGoalCard> commonGoalCards = commonGoalDeck.drawCommonGoalCard(numGoalCards);
        List<Player> players = new ArrayList<>();
        for (String nickname :
                nicknames) {
            players.add(new Player(nickname,personalGoalCardsGame.remove(0)));
        }
        HashMap<CommonGoalCard,List<ScoringToken>> commonGoal = new HashMap<>();
        for (CommonGoalCard x : commonGoalCards) {
            commonGoal.put(x, createTokensCommonGoalCard(x.getId(),numPlayerGame));
        }
        TileBag tileBag = new TileBag();

        this.game.setupGame(players, new Board(numPlayerGame),commonGoal,tileBag,ModelState.WAITING_SELECTION_TILE,gameName);

        try {
            this.game.getBoard().refillBoard(this.getNumPlayerGame(), this.game.getTileBag());
        } catch (WrongArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    private LinkedList<ScoringToken> createTokensCommonGoalCard(String id, int numPlayer) {
        LinkedList<ScoringToken> tokens = new LinkedList<>();
        switch (numPlayer) {
            case 2 -> {
                tokens.add(new ScoringToken(8, id));
                tokens.add(new ScoringToken(4, id));
            }
            case 3 -> {
                tokens.add(new ScoringToken(8, id));
                tokens.add(new ScoringToken(6, id));
                tokens.add(new ScoringToken(4, id));
            }
            case 4 -> {
                tokens.add(new ScoringToken(8, id));
                tokens.add(new ScoringToken(6, id));
                tokens.add(new ScoringToken(4, id));
                tokens.add(new ScoringToken(2, id));
            }
        }
        return tokens;
    }


    private void endGame() {
        this.game.setModelState(ModelState.END_GAME);
    }

    private void checkWinner() {
        Player p = this.game.getPlayers().stream().reduce( (a, b) -> {
            try {
                return a.getTotalPoints() > b.getTotalPoints() ? a:b;
            } catch (WrongArgumentException e) {
                throw new RuntimeException(e);
            }
        }).orElse(null);
        try {
            this.game.setWinner(p);
        } catch (WrongArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkEndGameBookShelfFull() {
        if(this.game.getPlayers().stream().anyMatch(x -> x.getBookshelf().isFull())) {
            this.game.getCurrPlayer().setHasFinalToken(true);
            endGame();
            checkWinner();
            return true;
        }
        return false;
    }


    /**
     * Set the player with the given nickname offline.
     * If the player is the current player, set the current player to the next online player, and
     * reset the Board if the player had some tiles in hand.
     * If there are no more online players, end the game.
     * @param nickname the nickname of the player to set offline
     */
    public void setPlayerOffline(String nickname) {
        // If the player is the current player, empty their hand and the selected column
        // Also, set the current player to the next online player (check that it's online).
        // Finally, set the model state to WAITING_SELECTION_TILE (beginning of next turn)
        try {
            if (this.game.getNumOnlinePlayers() == 2) {
                // Pause the game and send the update to the client
                this.game.saveState();
                this.game.setModelState(ModelState.PAUSE);
            } else if(this.game.getCurrPlayer().getNickname().equals(nickname)) {
                for (LocatedTile tile : this.game.getCurrPlayer().getTilesPicked()) {
                    // Put the tile back in the board
                    this.game.getBoard().setTile(tile.getRow(), tile.getCol(), new Tile(tile.getItemType(), tile.getItemId()));
                }
                this.game.getCurrPlayer().clearHand();
                this.game.getCurrPlayer().clearSelectedColumn();
                this.game.setCurrPlayer(this.game.getNextOnlinePlayer());
                this.game.setModelState(ModelState.WAITING_SELECTION_TILE);
            }
        } catch (WrongArgumentException e) {
            // This exception is thrown when there are no more online players
            this.endGame();
            throw new RuntimeException(e);
        }
        this.game.getPlayers().stream().filter(x -> x.getNickname().equals(nickname)).toList().get(0).setOnline(false);
        checkPlayersOnline();
    }

    public void setOnlinePlayer(String nickname) {
        // If the game is paused, resume it from the starting of the
        if (this.game.getModelState() == ModelState.PAUSE) {
            this.game.resumeStateAfterPause();
        }
        this.game.getPlayers().stream().filter(x -> x.getNickname().equals(nickname)).toList().get(0).setOnline(true);
        if(game.getNumOnlinePlayers() > 1) {
            if(isTimerRunning())
                stopTimer();
        }
    }

    private void checkPlayersOnline() {
        switch (this.game.getNumOnlinePlayers()) {
            case 0 -> endGame();
            case 1 -> startTimer();
        }
    }

    /**
     * Queue a command to be executed. This will add the command to the command queue in a separate thread
     * and execute it as soon as the executorService is available.
     * The ExecutorService is a single thread executor, so commands will be executed in the order they are queued.
     * @param queuedCommand The command to queue
     * @param queuedEvent The event that triggered the command
     */
    public void queueAndExecuteCommand(CommandMessage queuedCommand, UserInputEvent queuedEvent) {
        commandExecutor.execute(() -> {
            executeCommand(queuedCommand, queuedEvent);
            System.out.println("Executed command " + queuedCommand.getClass().getSimpleName());
        });
    }

    /**
     * Make the executor service execute a generic Runnable.
     * @param instruction The Runnable (such as a lambda function) to execute
     */
    public void queueAndExecuteInstruction(Runnable instruction) {
        commandExecutor.execute(instruction);
    }

    public void executeCommand(CommandMessage command, UserInputEvent t) {
        Command c;
        try {
            if (this.game.getModelState() == ModelState.PAUSE)
                throw new WrongTurnException("The game is paused due to disconnection from the other clients.");

            switch (t) {
                case SELECTED_TILES -> c = new PickTilesCommand(game.getBoard(), game.getCurrPlayer(), (PickedTilesCommandMessage) command, this.game.getModelState());
                case SELECTED_HAND_TILE -> c = new SelectTileFromHandCommand(game.getCurrPlayer(), (SelectedTileFromHandCommandMessage) command, this.game.getModelState());
                case SELECTED_BOOKSHELF_COLUMN -> c = new SelectColumnCommand(game.getCurrPlayer(), (SelectedColumnMessage) command, this.game.getModelState());
                default -> throw new InvalidCommand();
            }
            c.execute();
            game.resetErrorState();
            nextState();
        } catch (WrongTurnException e) {
            game.setErrorState(command.getNickname(), "Wait for your turn to perform this action. " + e.getMessage());
        } catch (InvalidCommand e) {
            game.setErrorState(command.getNickname(), "You tried to perform the wrong action: " + e.getMessage());
        } catch (WrongArgumentException e){
            game.setErrorState(command.getNickname(), "Your request has an invalid argument: " + e.getMessage());
        }
    }

    private void checkState(UserInputEvent t) throws InvalidCommand{
        ModelState currentGameState = game.getModelState();
        if(currentGameState == ModelState.WAITING_SELECTION_TILE && t != UserInputEvent.SELECTED_TILES) throw new InvalidCommand("waiting for Tile Selection ");
        if(currentGameState == ModelState.WAITING_SELECTION_BOOKSHELF_COLUMN && t != UserInputEvent.SELECTED_BOOKSHELF_COLUMN) throw new InvalidCommand("waiting for Column Selection ");
        if(currentGameState == ModelState.END_GAME) throw new InvalidCommand("game ended");
        if(currentGameState == ModelState.WAITING_3_SELECTION_TILE_FROM_HAND && t != UserInputEvent.SELECTED_HAND_TILE) throw new InvalidCommand("waiting for Tile Selection Hand ");
        if(currentGameState == ModelState.WAITING_2_SELECTION_TILE_FROM_HAND && t != UserInputEvent.SELECTED_HAND_TILE) throw new InvalidCommand("waiting for Tile Selection Hand ");
        if(currentGameState == ModelState.WAITING_1_SELECTION_TILE_FROM_HAND && t != UserInputEvent.SELECTED_HAND_TILE) throw new InvalidCommand("waiting for Tile Selection Hand ");
    }

    private void nextState() throws WrongArgumentException {
        ModelState currentGameState = game.getModelState();
        ModelState nextState = null;
        switch (currentGameState) {
            case WAITING_SELECTION_TILE:
                nextState = ModelState.WAITING_SELECTION_BOOKSHELF_COLUMN;
                break;
            case WAITING_3_SELECTION_TILE_FROM_HAND:
                CheckTokenAchievement();
                nextState = ModelState.WAITING_2_SELECTION_TILE_FROM_HAND;
                break;
            case WAITING_2_SELECTION_TILE_FROM_HAND:
                CheckTokenAchievement();
                nextState = ModelState.WAITING_1_SELECTION_TILE_FROM_HAND;
                break;
            case WAITING_1_SELECTION_TILE_FROM_HAND:
                CheckTokenAchievement();
                if(checkEndGameBookShelfFull()) {
                    nextState = ModelState.END_GAME;
                }else {
                    // TODO optimize this with a check to send the player update only once
                    try {
                        nextState = ModelState.WAITING_SELECTION_TILE;
                        game.setModelState(nextState);
                        game.setCurrPlayer(game.getNextPlayer());
                    } catch (WrongArgumentException e) {
                        throw new RuntimeException(e);
                    }
                    while(!game.getCurrPlayer().isOnline()) {
                        try {
                            game.setCurrPlayer(game.getNextPlayer());
                        } catch (WrongArgumentException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    updateEndTurn();
                }
                break;
            case WAITING_SELECTION_BOOKSHELF_COLUMN:
                switch (this.game.getCurrPlayer().getTilesPicked().size()){
                    case 3:
                        nextState = ModelState.WAITING_3_SELECTION_TILE_FROM_HAND;
                        break;
                    case 2:
                        nextState = ModelState.WAITING_2_SELECTION_TILE_FROM_HAND;
                        break;
                    case 1:
                        nextState = ModelState.WAITING_1_SELECTION_TILE_FROM_HAND;
                        break;
                }
                break;
            case END_GAME:
                nextState = ModelState.END_GAME;
                break;
        }
        game.setModelState(nextState);
    }

    public void updateEndTurn() throws WrongArgumentException {
        if(game.getBoard().isRefillNeeded()) {
            this.game.getBoard().refillBoard(this.getNumPlayerGame(), this.game.getTileBag());
        }
    }

    public void CheckTokenAchievement() throws WrongArgumentException {
        for(CommonGoalCard x: this.game.getCommonGoals()) {
            if(x.checkGoalSatisfied(this.game.getCurrPlayer().getBookshelf())) {
                this.game.getCurrPlayer().addScoringToken(this.game.popTopScoringToken(x));
            }
        }
    }


    public void addPlayer(String nickname) throws IllegalArgumentException{
        if (nicknames.contains(nickname))
            throw new IllegalArgumentException("Player already exists in the game");
        this.nicknames.add(nickname);
    }

    public void removePlayer(String nickname) {
        if(this.game != null)
            this.nicknames.remove(nickname);
    }

    public void removeAllPlayer() {
        this.nicknames = new ArrayList<>();
    }

    public void deleteGame() {
        this.game = null;
        this.numPlayerGame = 0;
        this.numGoalCards = 0;
    }

    public Game getGame() {
        return game;
    }

    public List<String> getNicknames() {
        return nicknames;
    }

    public int getNumPlayerGame() {
        return numPlayerGame;
    }

    public boolean isGameCreated() {
        if (game == null) return false;
        return true;
    }

    public boolean isGamePlaying() {
        return isGameCreated() && game.isPlaying();
    }

    public String getGameName() {
        return gameName;
    }
}
