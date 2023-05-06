package org.myshelfie.controller;

import org.myshelfie.model.WrongArgumentException;
import org.myshelfie.network.messages.commandMessages.JoinGameMessage;

import java.util.HashMap;
import java.util.UUID;

public class JoinGameCommand implements Command{

    private HashMap<String,GameController> gameControllers;

    private String nickname;

    private String gameName;


    public JoinGameCommand(HashMap<String, GameController> gameControllers, JoinGameMessage message){;
        this.gameControllers = gameControllers;
        this.nickname = message.getNickname();
        this.gameName = message.getGameName();
    }

    @Override
    public void execute() throws IllegalArgumentException {
        if(!gameControllers.containsKey(gameName))
            throw new IllegalArgumentException("Game not found");
        if(gameControllers.get(gameName).isGameCreated())
            throw new IllegalArgumentException("Game not joinable");
        GameController gameController = gameControllers.get(gameName);
        gameController.addPlayer(nickname);
    }


}
