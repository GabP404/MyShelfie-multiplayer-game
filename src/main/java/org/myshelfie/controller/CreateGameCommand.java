package org.myshelfie.controller;

import org.myshelfie.model.WrongArgumentException;
import org.myshelfie.network.messages.commandMessages.CreateGameMessage;
import org.myshelfie.network.messages.commandMessages.PickedTilesCommandMessage;

import java.util.HashMap;
import java.util.UUID;

public class CreateGameCommand implements Command {

    private HashMap<String,GameController> gameControllers;

    private String nickname;

    private int numPlayerGame;

    private int numGoalCards;

    private String gameName;

    public  CreateGameCommand(HashMap<String,GameController> gameControllers, CreateGameMessage message){
        this.gameControllers = gameControllers;
        this.nickname = message.getNickname();
        this.numPlayerGame = message.getNumPlayers();
        this.numGoalCards = (message.isSimplifiedRules() == true) ? 1 : 2;
        this.gameName = message.getGameName();
    }

    @Override
    public void execute() throws IllegalArgumentException {
        if(gameControllers.containsKey(gameName))
            throw new IllegalArgumentException("Game already exists");
        GameController gameController = new GameController(gameName, numPlayerGame, numGoalCards);
        gameController.addPlayer(nickname);
        gameControllers.put(gameName,gameController);
    }




}
