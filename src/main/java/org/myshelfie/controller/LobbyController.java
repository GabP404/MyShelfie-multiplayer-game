package org.myshelfie.controller;

import org.myshelfie.model.PersonalGoalCard;
import org.myshelfie.model.PersonalGoalDeck;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LobbyController {

    private static LobbyController single_istance;

    private List<GameController> gameControllers;

    private LobbyController() {
        gameControllers = new ArrayList<>();
    }

    public static LobbyController getInstance(){
        if (single_istance == null) {
            single_istance = new LobbyController();
        }
        return single_istance;
    }

    public void createNewGame(int size, int rules, String nickname) {
        GameController gameController = new GameController(UUID.randomUUID(), size, rules);
        gameController.addPlayer(nickname);
        gameControllers.add(gameController);
    }

    public void addPlayerToLobby(String nickname, UUID uuid) {
        GameController g = (GameController) gameControllers.stream().filter(x -> x.getGameUuid().equals(uuid));
        // if (g == null) throws exception
        g.addPlayer(nickname);
        if(g.getNicknames().size() == g.getNumPlayerGame()) {
            try {
                g.createGame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //remove player lobby
    public void deleteGame(UUID uuid) {
        GameController g = (GameController) gameControllers.stream().filter(x -> x.getGameUuid().equals(uuid));
        gameControllers.remove(g);
    }

    public void checkPlayer() {
       //check gameController with 0 players inside
        //deleteGame();
    }

    public void removePlayerLobby() {}

}
