package org.myshelfie.controller;

import org.myshelfie.network.messages.commandMessages.CreateGameMessage;

import java.util.HashMap;

/**
 * This class implements a command (following the command design pattern) that creates a new game.
 */
public class CreateGameCommand implements Command {

    private final HashMap<String, GameController> gameControllers;

    private final int numPlayerGame;

    private final int numGoalCards;

    private final String gameName;

    public CreateGameCommand(HashMap<String,GameController> gameControllers, CreateGameMessage message){
        this.gameControllers = gameControllers;
        this.numPlayerGame = message.getNumPlayers();
        this.numGoalCards = (message.isSimplifiedRules()) ? 1 : 2;
        this.gameName = message.getGameName();
    }

    /**
     * Creates a new {@link org.myshelfie.controller.GameController} object with the given name, number of players and
     * number of common goal cards. Put that object in the map of controllers in {@link org.myshelfie.controller.LobbyController}.
     * @throws IllegalArgumentException if the game already exists
     */
    @Override
    public void execute() throws IllegalArgumentException {
        if(gameControllers.containsKey(gameName))
            throw new IllegalArgumentException("Game already exists");
        GameController gameController = new GameController(gameName, numPlayerGame, numGoalCards);
        gameControllers.put(gameName, gameController);
    }

}
