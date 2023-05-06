package org.myshelfie.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.myshelfie.model.*;
import org.myshelfie.network.messages.commandMessages.*;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameControllerTest {

    static GameController gameController;

    static String gameName = "gamename";

    @BeforeAll
    public static void setup() {
        gameController = new GameController();
    }

    @BeforeEach
    public void testCreatingGame() {
        int numPlayerGame = 4;
        int numGoalCard = 2;
        List<String> nicknames = new ArrayList<>();
        nicknames.add("User1");
        nicknames.add("User2");
        nicknames.add("User3");
        nicknames.add("User4");
        gameController.createGame(numPlayerGame,numGoalCard,nicknames.get(0));
        for (int i = 1; i < nicknames.size(); i++) {
            gameController.addPlayer(nicknames.get(i));
        }
        assertNotNull(gameController.getGame());
        assertEquals(gameController.getGame().getPlayers().size(), numPlayerGame);
        assertNotNull(gameController.getGame().getBoard());
    }


    @Test
    public void testExecuteCommandPickTilesCommand() throws InvalidCommand, WrongTurnException, WrongArgumentException {
        //Try selecting a list of tiles: (0, 3) and (0, 4)
        assertNotNull(gameController.getGame());
        Game game = gameController.getGame();
        List<LocatedTile> tiles = new ArrayList<>();
        assertNotNull(game.getBoard().getTile(0,3));
        assertNotNull(game.getBoard().getTile(0,4));
        tiles.add(new LocatedTile(game.getBoard().getTile(0,3).getItemType(),0,3));
        tiles.add(new LocatedTile(game.getBoard().getTile(0,4).getItemType(),0,4));
        assertNotNull(tiles.get(0));
        assertNotNull(tiles.get(1));

        // Select and execute the tiles from the board
        PickedTilesCommandMessage m = new PickedTilesCommandMessage("User1", gameName, tiles);
        CommandMessageWrapper wrapper = new CommandMessageWrapper(m, UserInputEvent.SELECTED_TILES);
        UserInputEvent messageType = wrapper.getType();
        CommandMessage messageCommand = wrapper.getMessage();
        gameController.executeCommand(messageCommand,messageType);
        assertNull(game.getBoard().getTile(0,3));
        assertNull(game.getBoard().getTile(0,4));
        assertEquals(game.getModelState(),ModelState.WAITING_SELECTION_BOOKSHELF_COLUMN);


        SelectedColumnMessage scm = new SelectedColumnMessage("User1", gameName, 0);
        wrapper = new CommandMessageWrapper(scm, UserInputEvent.SELECTED_BOOKSHELF_COLUMN);
        messageType = wrapper.getType();
        messageCommand = wrapper.getMessage();
        gameController.executeCommand(messageCommand,messageType);
        assertEquals(game.getModelState(),ModelState.WAITING_2_SELECTION_TILE_FROM_HAND);
        assertEquals(game.getCurrPlayer().getSelectedColumn(),0);

        List<Tile> x = game.getCurrPlayer().getTilesPicked();
        SelectedTileFromHandCommandMessage stfhc = new SelectedTileFromHandCommandMessage("User1",gameName, 0, x.get(1).getItemType());
        wrapper = new CommandMessageWrapper(stfhc, UserInputEvent.SELECTED_HAND_TILE);
        messageType = wrapper.getType();
        messageCommand = wrapper.getMessage();
        gameController.executeCommand(messageCommand,messageType);
        assertEquals(game.getModelState(),ModelState.WAITING_1_SELECTION_TILE_FROM_HAND);


        Player p = game.getCurrPlayer();
        SelectedTileFromHandCommandMessage stfhc2 = new SelectedTileFromHandCommandMessage("User1", gameName, 0, x.get(0).getItemType());
        wrapper = new CommandMessageWrapper(stfhc2, UserInputEvent.SELECTED_HAND_TILE);
        messageType = wrapper.getType();
        messageCommand = wrapper.getMessage();
        gameController.executeCommand(messageCommand,messageType);
        assertEquals(game.getModelState(),ModelState.WAITING_SELECTION_TILE);
        assertNotEquals(p,game.getCurrPlayer());
    }




}
