package org.myshelfie.controller;

import org.myshelfie.model.Game;
import org.myshelfie.model.PersonalGoalCard;
import org.myshelfie.model.PersonalGoalDeck;
import org.myshelfie.network.messages.commandMessages.CommandMessage;
import org.myshelfie.network.messages.commandMessages.CreateGameMessage;
import org.myshelfie.network.messages.commandMessages.JoinGameMessage;
import org.myshelfie.network.messages.commandMessages.UserInputEvent;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LobbyController {


    // TODO Disconnessione da lobby
    private static LobbyController single_istance;

    private HashMap<String,GameController> gameControllers;

    public LobbyController() {
        gameControllers = new HashMap<>();
    }

    public static LobbyController getInstance(){
        if (single_istance == null) {
            single_istance = new LobbyController();
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

    public String createGame(CreateGameMessage message) throws IllegalArgumentException {
        CreateGameCommand c = new CreateGameCommand(gameControllers, message);
        c.execute();
        return message.getGameName();
    }

    public String joinGame(JoinGameMessage message) throws IllegalArgumentException {
        JoinGameCommand c = new JoinGameCommand(gameControllers, message);
        c.execute();
        return message.getGameName();
    }

}
