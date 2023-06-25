package org.myshelfie.controller;

import org.myshelfie.network.messages.commandMessages.JoinGameMessage;

import java.util.HashMap;

public class JoinGameCommand implements Command{

    private final HashMap<String,GameController> gameControllers;
    private final String nickname;
    private final String gameName;


    public JoinGameCommand(HashMap<String, GameController> gameControllers, JoinGameMessage message){;
        this.gameControllers = gameControllers;
        this.nickname = message.getNickname();
        this.gameName = message.getGameName();
    }

    /**
     * Check if the game is joinable and add the player to the list of players of the corresponding {@link GameController}
     * @throws IllegalArgumentException If the game is not found or not joinable
     */
    @Override
    public void execute() throws IllegalArgumentException {
        if(!gameControllers.containsKey(gameName))
            throw new IllegalArgumentException("Game not found");
        if(!gameControllers.get(gameName).isGameCreated())
            throw new IllegalArgumentException("Game not joinable");
        if(gameControllers.get(gameName).getGame().isPlaying())
            throw new IllegalArgumentException("Game not joinable");
        GameController gameController = gameControllers.get(gameName);
        gameController.addPlayer(nickname);
    }
}
