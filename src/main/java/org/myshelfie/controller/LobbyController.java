package org.myshelfie.controller;

import org.myshelfie.model.Game;
import org.myshelfie.model.PersonalGoalCard;
import org.myshelfie.model.PersonalGoalDeck;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LobbyController {

    private static LobbyController single_istance;

    private HashMap<UUID,GameController> gameControllers;

    private LobbyController() {
        gameControllers = new HashMap<>();
    }

    public static LobbyController getInstance(){
        if (single_istance == null) {
            single_istance = new LobbyController();
        }
        return single_istance;
    }

    public void createNewGame(int numPlayerGame, int  numGoalCards, String nickname) {
        UUID uuid = UUID.randomUUID();
        GameController gameController = new GameController(uuid, numPlayerGame, numGoalCards);
        gameController.addPlayer(nickname);
        gameControllers.put(uuid,gameController);
    }

    public void addPlayerToLobby(String nickname, UUID uuid) throws IOException, URISyntaxException {
        GameController gameController = gameControllers.get(uuid);
        gameController.addPlayer(nickname);
    }

    //remove player lobby
    public void deleteGame(UUID uuid) {
        GameController gameController = gameControllers.get(uuid);
        gameControllers.remove(gameController);
    }


    public void removePlayerLobby(String nickname, UUID uuid) {
        GameController gameController = gameControllers.get(uuid);
        gameController.removePlayer(nickname);
    }

}
