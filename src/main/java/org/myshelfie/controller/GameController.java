package org.myshelfie.controller;

import org.myshelfie.model.*;
import org.myshelfie.network.messages.commandMessages.*;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.server.Server;
import org.myshelfie.network.client.UserInputEvent;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is the controller of a game. It contains the game logic and state.
 */
public class GameController implements Serializable {
    private transient Timer timer; // declared as transient to not serialize it
    private final int timeout;
    private boolean isRunning;
    private transient ExecutorService commandExecutor;     // declared as transient to not serialize it
    private final String gameName;
    private final Game game;
    private final List<String> nicknames;
    private final int numPlayerGame;
    private final int numCommonGoals;

    /**
     * Inner class used to represent a game. Objects of this class are sent to the clients
     * to let them know the available games and their state. See {@link Server#updatePreGame}.
     */
    public static class GameDefinition implements Serializable {
        private final String gameName;
        private final int maxPlayers;
        private final List<String> nicknames;
        private final boolean simplifyRules;

        /**
         * Constructor used to create a GameDefinition from a GameController.
         * @param gc The GameController used to retrieve the information.
         */
        public GameDefinition(GameController gc) {
            this.gameName = gc.getGameName();
            this.maxPlayers = gc.getNumPlayerGame();
            this.nicknames = new ArrayList<>(gc.getNicknames());
            this.simplifyRules = gc.getNumCommonGoals() == 1;
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

    /**
     * Method used to scheduler the timer.
     */
    public void startTimer() {
        timer = new Timer();
        timer.schedule(new GameTimerTask(), this.timeout); // 1 minute = 60,000 milliseconds
        isRunning = true;
    }

    /**
     * Method used to stop and cancel the timer. This is called in {@link #setOnlinePlayer} when a disconnected player reconnects.
     */
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            isRunning = false;
        }
    }

    public boolean isTimerRunning() {
        return isRunning;
    }

    /**
     * Timer task used to delete a game a certain amount of time after only one player has been online (with game paused).
     * When the {@link #run()} method is called, the END_GAME event is sent to the client and the game is deleted.
     */
    private class GameTimerTask extends TimerTask {
        @Override
        public void run() {
            endGame();
            GameController.this.findWinners();
            Server.eventManager.notify(GameEvent.GAME_END, getGame());
            Server.eventManager.sendToClients();
            isRunning = false;
            LobbyController.removeGameWhenFinished(getGameName());
        }
    }


    public int getNumCommonGoals() {
        return numCommonGoals;
    }

    /**
     * Constructor for the GameController.
     * @param gameName The name of the game.
     * @param numPlayerGame The number of players in the game.
     * @param numGoalCards The number of common goal cards in the game (depends on rules).
     */
    public GameController(String gameName, int numPlayerGame, int numGoalCards) {
        this.gameName = gameName;
        this.nicknames = new ArrayList<>();
        this.numPlayerGame = numPlayerGame;
        this.numCommonGoals = numGoalCards;
        this.timeout = Configuration.getTimerTimeout();
        this.game = new Game();
        createCommandExecutor();
    }

    /**
     * Creates a new command executor.
     */
    public void createCommandExecutor() {
        this.commandExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * Set up the game and start it by performing the first action, i.e. refilling the board.
     */
    public void setupGame() throws IOException, URISyntaxException {
        // Prepares all the game elements
        CommonGoalDeck commonGoalDeck = CommonGoalDeck.getInstance();
        PersonalGoalDeck personalGoalDeck = PersonalGoalDeck.getInstance();
        List<PersonalGoalCard> personalGoalCardsGame = personalGoalDeck.draw(numPlayerGame);
        List<CommonGoalCard> commonGoalCards = commonGoalDeck.drawCommonGoalCard(numCommonGoals);
        List<Player> players = new ArrayList<>();
        for (String nickname : nicknames) {
            players.add(new Player(nickname,personalGoalCardsGame.remove(0)));
        }
        HashMap<CommonGoalCard,List<ScoringToken>> commonGoal = new HashMap<>();
        for (CommonGoalCard x : commonGoalCards) {
            commonGoal.put(x, createTokensCommonGoalCard(x.getId(),numPlayerGame));
        }
        TileBag tileBag = new TileBag();

        // Assign all the elements to the game object
        this.game.setupGame(players, new Board(numPlayerGame),commonGoal,tileBag,ModelState.WAITING_SELECTION_TILE,gameName);

        // Refill the board
        try {
            this.game.getBoard().refillBoard(this.getNumPlayerGame(), this.game.getTileBag());
        } catch (WrongArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Utility method used to create the scoring tokens stack for a common goal card.
     * @param id The id of the common goal card.
     * @param numPlayer The number of players in the game.
     * @return List of scoring tokens.
     */
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

    /**
     * Method used to set the game state to END_GAME.
     */
    private void endGame() {
        this.game.setModelState(ModelState.END_GAME);
    }


    /**
     * Method used to check if the game is ended by looking if any player has completed the bookshelf.
     * @return True if the game is ended, false otherwise.
     */
    private boolean checkEndGameBookShelfFull() {
        if(this.game.getPlayers().stream().anyMatch(x -> x.getBookshelf().isFull())) {
            this.game.getCurrPlayer().setHasFinalToken(true);
            endGame();
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

    /**
     * Switch the player status to online. If the game was paused it's resumed and the timer is stopped.
     * @param nickname The nickname of the player to set online.
     */
    public void setOnlinePlayer(String nickname) {
        // If the game was paused, resume it
        this.game.getPlayers().stream().filter(x -> x.getNickname().equals(nickname)).toList().get(0).setOnline(true);
        if(game.getNumOnlinePlayers() > 1) {
            if (this.game.getModelState() == ModelState.PAUSE) {
                this.game.resumeStateAfterPause();

                // If the current player is still offline, set it to the next online player so that the others can continue playing
                if (!this.game.getCurrPlayer().isOnline()) {
                    // Force setting the player online and offline again, to trigger the handling
                    // of the turns and possibly clearing the hand of the player
                    // Note that this does not send an update to the players, as
                    // `sendToClients` is called only at the end of the method
                    this.game.getCurrPlayer().setOnline(true);
                    setPlayerOffline(this.game.getCurrPlayer().getNickname());
                }
            }
            if(isTimerRunning())
                stopTimer();
        }
    }

    /**
     * Utility method that checks the number of online players and starts the timer if there is only one player online.
     * It ends the game if there are no more online players.
     */
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
        });
    }

    /**
     * Make the executor service execute a generic Runnable.
     * @param instruction The Runnable (such as a lambda function) to execute
     */
    public void queueAndExecuteInstruction(Runnable instruction) {
        commandExecutor.execute(instruction);
    }

    /**
     * Main method of the execution flow of the GameController.
     * Given a command and the event that triggered it, it creates the corresponding Command object and executes it.
     * Reset the error state if everything went fine, the makes the game state transition to the next state.
     * @param command The command to execute
     * @param t The event that triggered the command
     */
    public void executeCommand(CommandMessage command, UserInputEvent t) {
        Command c;
        try {
            if (this.game.getModelState() == ModelState.PAUSE)
                throw new WrongTurnException("The game is paused due to disconnection from the other clients.");

            switch (t) {
                case SELECTED_TILES -> c = new PickTilesCommand(game.getBoard(), game.getCurrPlayer(), (PickedTilesCommandMessage) command, this.game.getModelState());
                case SELECTED_HAND_TILE -> c = new SelectTileFromHandCommand(game.getCurrPlayer(), (SelectedTileFromHandCommandMessage) command, this.game.getModelState());
                case SELECTED_BOOKSHELF_COLUMN -> c = new SelectColumnCommand(game.getCurrPlayer(), (SelectedColumnMessage) command, this.game.getModelState());
                default -> throw new InvalidCommandException();
            }
            c.execute();
            game.resetErrorState();
            nextState();
        } catch (WrongTurnException e) {
            game.setErrorState(command.getNickname(), "Wait for your turn to perform this action. " + e.getMessage());
        } catch (InvalidCommandException e) {
            game.setErrorState(command.getNickname(), "You tried to perform the wrong action: " + e.getMessage());
        } catch (WrongArgumentException e){
            game.setErrorState(command.getNickname(), "Your request has an invalid argument: " + e.getMessage());
        }
    }

    /**
     * Method responsible for the transition of the game's state.
     * @throws WrongArgumentException If the player that will be set as current player is invalid.
     */
    private void nextState() throws WrongArgumentException {
        ModelState currentGameState = game.getModelState();
        ModelState nextState = null;
        switch (currentGameState) {
            case WAITING_SELECTION_TILE -> nextState = ModelState.WAITING_SELECTION_BOOKSHELF_COLUMN;
            case WAITING_3_SELECTION_TILE_FROM_HAND -> {
                checkTokenAchievement();
                nextState = ModelState.WAITING_2_SELECTION_TILE_FROM_HAND;
            }
            case WAITING_2_SELECTION_TILE_FROM_HAND -> {
                checkTokenAchievement();
                nextState = ModelState.WAITING_1_SELECTION_TILE_FROM_HAND;
            }
            case WAITING_1_SELECTION_TILE_FROM_HAND -> {
                checkTokenAchievement();
                if (checkEndGameBookShelfFull()) {
                    findWinners();
                    nextState = ModelState.END_GAME;
                } else {
                    try {
                        nextState = ModelState.WAITING_SELECTION_TILE;
                        game.setModelState(nextState);
                        game.setCurrPlayer(game.getNextPlayer());
                    } catch (WrongArgumentException e) {
                        throw new RuntimeException(e);
                    }
                    while (!game.getCurrPlayer().isOnline()) {
                        try {
                            game.setCurrPlayer(game.getNextPlayer());
                        } catch (WrongArgumentException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    updateEndTurn();
                }
            }
            case WAITING_SELECTION_BOOKSHELF_COLUMN ->
                    nextState = switch (this.game.getCurrPlayer().getTilesPicked().size()) {
                        case 3 -> ModelState.WAITING_3_SELECTION_TILE_FROM_HAND;
                        case 2 -> ModelState.WAITING_2_SELECTION_TILE_FROM_HAND;
                        case 1 -> ModelState.WAITING_1_SELECTION_TILE_FROM_HAND;
                        default -> null;
                    };
            case END_GAME -> {
                findWinners();
                nextState = ModelState.END_GAME;
            }
        }
        game.setModelState(nextState);
    }

    /**
     * Method that checks if the board needs to be refilled and do so if needed.
     * @throws WrongArgumentException If the tiles bag is empty (should never happen)
     */
    public void updateEndTurn() throws WrongArgumentException {
        if(game.getBoard().isRefillNeeded()) {
            this.game.getBoard().refillBoard(this.getNumPlayerGame(), this.game.getTileBag());
        }
    }

    /**
     * Setting the winner attribute to true of the players who have the maximum points and are online.
     * Draw is possible
     */
    private void findWinners(){
        int maxPointsOnlinePlayers = this.game.getPlayers().stream().filter(Player::isOnline).mapToInt(x -> x.getTotalPoints()).max().getAsInt();
        for (Player p : this.game.getPlayers()) {
            if (p.getTotalPoints() == maxPointsOnlinePlayers && p.isOnline()) {
                p.setWinner(true);
                System.out.println(p.getNickname() + " is the winner!");
            }
        }

    }

    /**
     * Method that checks if any player has completed a common goal and gives them the corresponding token.
     */
    public void checkTokenAchievement() throws WrongArgumentException {
        Player p = this.game.getCurrPlayer();
        for(CommonGoalCard x: this.game.getCommonGoals()) {
            if (
                p.getCommonGoalTokens().stream().noneMatch(t -> t.getCommonGoalId().equals(x.getId())) &&
                x.checkGoalSatisfied(p.getBookshelf())
            )
                p.addScoringToken(this.game.popTopScoringToken(x));
        }
    }


    /**
     * Method use during game creation to add a player to the list of players.
     * @param nickname The nickname of the player to add
     * @throws IllegalArgumentException If the player already exists in the game
     */
    public void addPlayer(String nickname) throws IllegalArgumentException{
        if (nicknames.contains(nickname))
            throw new IllegalArgumentException("Player already exists in the game");
        this.nicknames.add(nickname);
    }

    /**
     * Method use during game creation to remove a player from the list of players.
     * @param nickname The nickname of the player to remove
     */
    public void removePlayer(String nickname) {
        if (this.game != null)
            this.nicknames.remove(nickname);
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
        return game != null;
    }

    public boolean isGamePlaying() {
        return isGameCreated() && game.isPlaying();
    }

    public String getGameName() {
        return gameName;
    }
}
