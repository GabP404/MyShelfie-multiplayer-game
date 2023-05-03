package org.myshelfie.controller;

import org.myshelfie.model.Game;
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

    public void addPlayerToLobby(String nickname, UUID uuid) throws IOException, URISyntaxException {
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
        checkPlayer(g);
    }

    //remove player lobby
    public void deleteGame(UUID uuid) {
        GameController g = (GameController) gameControllers.stream().filter(x -> x.getGameUuid().equals(uuid));
        gameControllers.remove(g);
    }

    private void checkPlayer(GameController g) throws IOException, URISyntaxException {
        if(g.getNicknames().size() == g.getNumPlayerGame()) {
            g.createGame();
        }
    }

    public void removePlayerLobby(String nickname, UUID uuid) {
        for (GameController g :
                gameControllers) {
            if(g.getGameUuid() == uuid && !g.isGameCreated()) {
                g.getNicknames().remove(nickname);
            }
        }
    }

}
