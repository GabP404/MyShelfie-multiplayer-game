package org.myshelfie.controller;

import org.myshelfie.model.Game;
import org.myshelfie.model.PersonalGoalCard;
import org.myshelfie.model.PersonalGoalDeck;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.commandMessages.CommandMessage;
import org.myshelfie.network.messages.commandMessages.CreateGameMessage;
import org.myshelfie.network.messages.commandMessages.JoinGameMessage;
import org.myshelfie.network.messages.commandMessages.UserInputEvent;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.server.GameListener;
import org.myshelfie.network.server.Server;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LobbyController {
    private Server server;

    // TODO Disconnessione da lobby
    private static LobbyController single_istance;

    private HashMap<String,GameController> gameControllers;

    private LobbyController(Server server) {
        this.server = server;
        gameControllers = new HashMap<>();
    }

    public static LobbyController getInstance(Server server){
        if (single_istance == null) {
            single_istance = new LobbyController(server);
        }
        return single_istance;
    }
    public void executeCommand(CommandMessage command, UserInputEvent t) {
        gameControllers.get(command.getGameName()).executeCommand(command, t);
    }


    //remove player lobby
    public void deleteGame(String gameName) {
        GameController gameController = gameControllers.get(gameName);
        gameControllers.remove(gameController);
    }

    public void setPlayerOffline(String nickname, String gameName) {
        GameController gameController = gameControllers.get(gameName);
        gameController.setPlayerOffline(nickname);
    }

    public void setPlayerOffline(String nickname) {
        for (GameController g : gameControllers.values()) {
            if (g.getNicknames().contains(nickname))
            g.setPlayerOffline(nickname);
        }
    }

    public void removePlayerLobby(String nickname, String gameName) {
        GameController gameController = gameControllers.get(gameName);
        gameController.removePlayer(nickname);
    }

    public List<GameController.GameDefinition> getGames() {
        ArrayList<GameController.GameDefinition> l = new ArrayList<>();
        for (GameController g : gameControllers.values()) {
            l.add(new GameController.GameDefinition(g));
        }
        return l;
    }

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

    public void joinGame(JoinGameMessage message) throws IllegalArgumentException {
        JoinGameCommand c = new JoinGameCommand(gameControllers, message);
        c.execute();

        Client client = server.getClient(message.getNickname());
        GameController gameController = gameControllers.get(message.getGameName());
        Game gameToSubscribe = gameController.getGame();
        Server.eventManager.subscribe(GameEvent.class, new GameListener(this.server, client, gameToSubscribe));

        if (gameController.getNicknames().size() == gameController.getNumPlayerGame()) {
            try {
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.schedule(() -> {
                    try {
                        System.out.println("Setting game " + message.getGameName() + " up...");
                        gameController.setupGame();
                        System.out.println("Game set up!");
                    } catch (IOException | URISyntaxException e) {
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
}
