package org.myshelfie.controller;

import org.myshelfie.model.Game;
import org.myshelfie.model.ModelState;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.commandMessages.CommandMessage;
import org.myshelfie.network.messages.commandMessages.CreateGameMessage;
import org.myshelfie.network.messages.commandMessages.JoinGameMessage;
import org.myshelfie.network.client.UserInputEvent;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.server.GameListener;
import org.myshelfie.network.server.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * This class is the entry point of every command received by the server.
 * It manages all the games and the lobbies, and forwards the commands to the right {@link GameController}.
 */
public class LobbyController {
    private static Server server;

    private static HashMap<String,GameController> gameControllers;

    public LobbyController(Server server) {
        this.server = server;
        if(server.shouldResumeFromBackup()) {
            try {
                server.log(Level.INFO, "Backup option selected. Resuming from default backup file...");
                gameControllers = GameControllerSaver.load();
                //for each element in gameControllers, create a new Executor service
                for (GameController g : gameControllers.values()) {
                    g.createCommandExecutor();
                }
                server.log(Level.INFO, "Games resumed successfully! Waiting for players to reconnect...");
            } catch (Exception e) {
                server.log(
                        Level.WARNING,
                        "Exception occurred while resuming from backup file: " + e.getMessage() +
                        "\nNo big deal, I'll just create a new gameControllers map."
                );
                gameControllers = new HashMap<>();
            }
        }
        else {
            gameControllers = new HashMap<>();
        }
    }

    public void executeCommand(CommandMessage command, UserInputEvent t) {
        // Queue the command
        gameControllers.get(command.getGameName()).queueAndExecuteCommand(command, t);

        gameControllers.get(command.getGameName()).queueAndExecuteInstruction(
                () -> {
                    // Send the update to all the clients
                    Server.eventManager.sendToClients();
                }
        );

        gameControllers.get(command.getGameName()).queueAndExecuteInstruction(() -> removeGameWhenFinished(command.getGameName()));

        // Queue the operation of saving the server status.
        // This operation is done inside the executor thread at the end of every command so that the status is kept consistent.
        // The `save` method is synchronized so that only one thread at a time can access the file.
        gameControllers.get(command.getGameName()).queueAndExecuteInstruction(
                () -> {
                    try {
                        GameControllerSaver.save(gameControllers);
                    } catch (IOException e) {
                        server.log(Level.WARNING, "Exception occurred while saving gameControllers: " + e.getMessage());
                    }
                }
        );
    }

    public static void removeGameWhenFinished(String gameName) {
        // Delete the game if it has ended - the update has already been sent to the clients
        if (gameControllers.get(gameName).getGame().getModelState() == ModelState.END_GAME) {
            server.log(Level.INFO, "Removing game " + gameName + " from the server");
            // Unsubscribe all the clients that were listening to this game
            gameControllers.get(gameName).getGame().getPlayers().forEach(
                    (player) -> {
                        Client toUnregister = server.getClient(player.getNickname());
                        server.unregister(toUnregister);
                    }
            );
            // Clear the list of nicknames
            gameControllers.get(gameName).getNicknames().clear();
            // Delete the game from the map
            gameControllers.remove(gameName);
        }
    }

    //remove player lobby
    public void deleteGame(String gameName) {
        GameController gameController = gameControllers.remove(gameName);
    }


    /**
     * Returns the list of {@link GameController.GameDefinition} of all the available games.
     * @return The list of available games
     */
    public List<GameController.GameDefinition> getGames() {
        ArrayList<GameController.GameDefinition> l = new ArrayList<>();
        for (GameController g : gameControllers.values()) {
            l.add(new GameController.GameDefinition(g));
        }
        return l;
    }

    /**
     * Executes the {@link CreateGameCommand} to create a new game, adds the player to the list of players inside the game
     * and subscribes the client to the event listener to make it receive updates from that game.
     * @param message The message containing the information to create the game
     * @throws IllegalArgumentException If the game cannot be created
     */
    public void createGame(CreateGameMessage message) throws IllegalArgumentException {
        CreateGameCommand c = new CreateGameCommand(gameControllers, message);
        // The reference to the Game is created inside the execute method and stored in the GameController
        c.execute();

        Client client = server.getClient(message.getNickname());
        Game gameToSubscribe = gameControllers.get(message.getGameName()).getGame();
        // Add the player to the list of players inside the Game
        gameControllers.get(message.getGameName()).addPlayer(message.getNickname());
        // Subscribe the client to the event listener (if no exception is thrown)
        Server.eventManager.subscribe(GameEvent.class, new GameListener(this.server, client, gameToSubscribe));
    }

    /**
     * Executes the {@link JoinGameCommand} to join an existing game, adds the player to the list of players inside the game
     * and subscribes the client to the event listener to make it receive updates from that game.
     * Then checks if the game has reached the maximum number of players and set it up if so.
     * @param message The {@link JoinGameMessage} containing the name of the game to join and the nickname of the player
     * @throws IllegalArgumentException If the game is not found or not joinable
     */
    public void joinGame(JoinGameMessage message) throws IllegalArgumentException {
        JoinGameCommand c = new JoinGameCommand(gameControllers, message);
        c.execute();

        Client client = server.getClient(message.getNickname());
        GameController gameController = gameControllers.get(message.getGameName());

        // The game is full and already started, so no new player can join it.
        Game gameToSubscribe = gameController.getGame();
        if (gameToSubscribe != null && gameToSubscribe.isPlaying()) {
            return;
        }
        Server.eventManager.subscribe(GameEvent.class, new GameListener(this.server, client, gameToSubscribe));

        if (gameController.getNicknames().size() == gameController.getNumPlayerGame()) {
            try {
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.schedule(() -> {
                    try {
                        server.log(Level.FINE, "Setting game " + message.getGameName() + " up...");
                        gameController.setupGame();
                        // Send the update to all the clients (containing the first update, i.e. board refill)
                        gameController.queueAndExecuteInstruction(() -> {
                            Server.eventManager.sendToClients();
                        });
                        server.log(Level.INFO, "Game " + message.getGameName() + " set up!");
                    } catch (Exception e) {
                        server.log(Level.SEVERE, "Exception while setting game up: " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                }, 2, TimeUnit.SECONDS);

                executorService.shutdown();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Game retrieveGame(String gameName) {
        return gameControllers.get(gameName).getGame();
    }

    /**
     * Returns the name of the game in which the player with that nickname is currently playing.
     * @param nickname The nickname of the player.
     * @return The name of the game in which the player with that nickname is currently playing.
     */
    public String getGameNameFromPlayerNickname(String nickname) {
        for (GameController gameController : gameControllers.values()) {
            if (gameController.getNicknames().contains(nickname)) {
                return gameController.getGameName();
            }
        }
        return null;
    }

    /**
     * Handles the disconnection of a client: it sets the player offline if the game
     * has already started, whereas it removes it from the lobby if the game has not, but
     * the player has already joined the lobby.
     * @param nickname The nickname of the player that has disconnected.
     */
    public void handleClientDisconnection(String nickname) {
        String gameName = getGameNameFromPlayerNickname(nickname);
        if (gameName == null) {
            //The player had not joined any game or lobby,
            //so there is nothing to do in the LobbyController.
            return;
        }

        GameController gameController = gameControllers.get(gameName);
        if (gameController.isGamePlaying()) {
            //The game has already started, so the player is set offline.
            gameController.setPlayerOffline(nickname);
            gameController.queueAndExecuteInstruction(() -> {
                // Send the update to all the clients
                Server.eventManager.sendToClients();
            });
        } else {
            //The game has not started yet, so the player is removed from the lobby.
            gameController.removePlayer(nickname);
        }
    }

    /**
     * Handles the reconnection of a client: if possible, it will add them back to the game,
     * otherwise it will add them back to the lobby to which they were connected.
     * If it is not possible to add the player back to the game, it will return false.
     *
     * @param nickname The nickname of the player that had disconnected.
     */
    public boolean handleClientReconnection(String nickname) {
        String gameName = getGameNameFromPlayerNickname(nickname);
        if (gameName == null) {
            // No started game were found for the player, so there is nowhere to add it back to.
            return false;
        }

        GameController gameController = gameControllers.get(gameName);
        if (gameController.isGameCreated()) {
            server.log(Level.INFO, "Player " + nickname + " reconnected to game " + gameName + "!");

            // Subscribe the client to the event listener
            Game gameToSubscribe = gameController.getGame();
            Client client = server.getClient(nickname);
            if (client == null) {
                server.log(Level.WARNING, "Client " + nickname + " not found!");
                return false;
            }
            Server.eventManager.subscribe(GameEvent.class, new GameListener(this.server, client, gameToSubscribe));

            //The game has already started, so the player is set back online after 1.5 seconds,
            //and the GameView notify is automatically triggered to all the clients
            try {
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.schedule(() -> {
                    gameController.setOnlinePlayer(nickname);
                    Server.eventManager.sendToClients();
                }, 1500, TimeUnit.MILLISECONDS);

                executorService.shutdown();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return true;
        }

        //The game was not started yet, so no need to send a GameView Update
        return false;
    }
}
