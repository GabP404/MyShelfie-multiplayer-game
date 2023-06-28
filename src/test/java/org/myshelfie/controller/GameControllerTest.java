package org.myshelfie.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.myshelfie.model.*;
import org.myshelfie.network.messages.commandMessages.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {

    static GameController gameController;

    static String gameName = "gamename";

    @BeforeEach
    public void setup() {
        gameController = new GameController("testGame", 4, 2);
        List<String> nicknames = new ArrayList<>();
        nicknames.add("User1");
        nicknames.add("User2");
        nicknames.add("User3");
        nicknames.add("User4");
        for (String nickname : nicknames) {
            gameController.addPlayer(nickname);
        }
        try {
            gameController.setupGame();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

        List<LocatedTile> x = game.getCurrPlayer().getTilesPicked();
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

    @Test
    public void ThreeTilesInHand() throws WrongArgumentException {
        List<LocatedTile> handT = new ArrayList<>();
        handT.add(new LocatedTile(ItemType.BOOK,0,0));
        handT.add(new LocatedTile(ItemType.CAT,0,0));
        handT.add(new LocatedTile(ItemType.TROPHY,0,0));
        gameController.getGame().getPlayers().get(0).setTilesPicked(handT);
        gameController.getGame().setModelState(ModelState.WAITING_3_SELECTION_TILE_FROM_HAND);

        SelectedTileFromHandCommandMessage pickMess = new SelectedTileFromHandCommandMessage("User1", gameName, 0, ItemType.BOOK);
        CommandMessageWrapper wrapper = new CommandMessageWrapper(pickMess, UserInputEvent.SELECTED_HAND_TILE);
        UserInputEvent messageType = wrapper.getType();
        CommandMessage messageCommand = wrapper.getMessage();
        //without selecting a column
        gameController.executeCommand(messageCommand,messageType);

        //after selecting a column
        gameController.getGame().getPlayers().get(0).setSelectedColumn(0);
        gameController.executeCommand(messageCommand,messageType);

        pickMess = new SelectedTileFromHandCommandMessage("User1", gameName, 0, ItemType.CAT);
        wrapper = new CommandMessageWrapper(pickMess, UserInputEvent.SELECTED_HAND_TILE);
        messageType = wrapper.getType();
        messageCommand = wrapper.getMessage();
        gameController.executeCommand(messageCommand,messageType);

        pickMess = new SelectedTileFromHandCommandMessage("User1", gameName, 0, ItemType.TROPHY);
        wrapper = new CommandMessageWrapper(pickMess, UserInputEvent.SELECTED_HAND_TILE);
        messageType = wrapper.getType();
        messageCommand = wrapper.getMessage();
        gameController.executeCommand(messageCommand,messageType);

        assertEquals(gameController.getGame().getModelState(),ModelState.WAITING_SELECTION_TILE);
        assertEquals(0,gameController.getGame().getPlayers().get(0).getTilesPicked().size());
        assertEquals(ItemType.BOOK,gameController.getGame().getPlayers().get(0).getBookshelf().getTile(5,0).getItemType());
    }


    @Test
    public void testEndTurn() throws WrongArgumentException, IOException, URISyntaxException {
        LocatedTile x = null;
        assertNotNull(gameController.getGame());
        gameController.setupGame();
        Game game = gameController.getGame();
        for (int i = 0; i < Board.DIMBOARD - 1; i++) {
            for (int j = 0; j < Board.DIMBOARD; j++) {
                game.getBoard().removeTile(i,j);
            }
        }
        assertNotNull(game.getBoard().getTile(Board.DIMBOARD - 1,4));
        Tile t = game.getBoard().removeTile(Board.DIMBOARD - 1,4);
        x = new LocatedTile(t.getItemType(),Board.DIMBOARD - 1,4);
        game.getCurrPlayer().addTilesPicked(x);
        game.setModelState(ModelState.WAITING_1_SELECTION_TILE_FROM_HAND);
        game.getCurrPlayer().setSelectedColumn(0);
        Player p = game.getCurrPlayer();
        SelectedTileFromHandCommandMessage stfhc2 = new SelectedTileFromHandCommandMessage(p.getNickname(), gameName, 0, x.getItemType());
        CommandMessageWrapper wrapper = new CommandMessageWrapper(stfhc2, UserInputEvent.SELECTED_HAND_TILE);
        UserInputEvent messageType = wrapper.getType();
        CommandMessage messageCommand = wrapper.getMessage();
        assertTrue(game.getBoard().isRefillNeeded());
        System.out.println(game.getBoard().isRefillNeeded());
        gameController.executeCommand(messageCommand,messageType);
        assertFalse(game.getBoard().isRefillNeeded());
    }

    @Test
    void startStopTimer() {
        gameController.startTimer();
        assertEquals(Boolean.TRUE,gameController.isTimerRunning());
        gameController.stopTimer();
        assertEquals(Boolean.FALSE,gameController.isTimerRunning());
    }

    @Test
    void generateCommonGoal() {
        //3 players, 1 common goal card
        gameController = new GameController("testCommonG", 3, 1);
        List<String> nicknames = new ArrayList<>();
        nicknames.add("User1");
        nicknames.add("User2");
        nicknames.add("User3");
        for (String nickname : nicknames) {
            gameController.addPlayer(nickname);
        }
        try {
            gameController.setupGame();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertDoesNotThrow(() -> gameController.getGame().getCommonGoals().get(0));
        assertEquals(1, gameController.getNumGoalCards());

    }

    @Test
    void checkEndGameBookShelfFullTest() throws WrongArgumentException {

        //two for loop nested, the first is 5 times, the second is 6 times
        for (int i = 0; i < Bookshelf.NUMCOLUMNS; i++) {
            for (int j = 0; j < Bookshelf.NUMROWS; j++) {
                if(i!= 0 || j!= 0)
                    gameController.getGame().getPlayers().get(0).getBookshelf().insertTile(new Tile(ItemType.BOOK,0),i);
            }
        }


        gameController.getGame().getPlayers().get(0).setSelectedColumn(0);

        //
        List<LocatedTile> handT = new ArrayList<>();
        handT.add(new LocatedTile(ItemType.BOOK,0,0));
        gameController.getGame().getPlayers().get(0).setTilesPicked(handT);
        gameController.getGame().setModelState(ModelState.WAITING_1_SELECTION_TILE_FROM_HAND);
        SelectedTileFromHandCommandMessage finMess = new SelectedTileFromHandCommandMessage("User1", gameName, 0, ItemType.BOOK);
        CommandMessageWrapper wrapper = new CommandMessageWrapper(finMess, UserInputEvent.SELECTED_HAND_TILE);
        UserInputEvent messageType = wrapper.getType();
        CommandMessage messageCommand = wrapper.getMessage();
        gameController.executeCommand(messageCommand,messageType);
        assertEquals(ModelState.END_GAME, gameController.getGame().getModelState());
        assertTrue(gameController.getGame().getPlayers().get(0).isWinner());

    }

    @Test
    void PauseAndReconnection()
    {
        gameController.getGame().setModelState(ModelState.PAUSE);
        gameController.setPlayerOffline("User2");
        assertFalse(gameController.getGame().getPlayers().get(1).isOnline());
        gameController.setOnlinePlayer("User2");
        assertTrue(gameController.getGame().getPlayers().get(1).isOnline());


        gameController.setPlayerOffline("User2");
        gameController.setPlayerOffline("User3");
        gameController.setPlayerOffline("User1");
        gameController.setOnlinePlayer("User2");
        gameController.setOnlinePlayer("User1");
        assertTrue(gameController.getGame().getPlayers().get(0).isOnline());
        assertTrue(gameController.getGame().getPlayers().get(1).isOnline());

        gameController.setPlayerOffline("User1");
        gameController.setPlayerOffline("User2");
        gameController.setPlayerOffline("User4");
        assertEquals(ModelState.END_GAME,gameController.getGame().getModelState());
    }

    @Test
    void getterTest()
    {
        assertDoesNotThrow(() -> gameController.getGame());
        List<String> nicknames = gameController.getNicknames();
        assertEquals(4,nicknames.size());
        assertEquals("User1",nicknames.get(0));
        assertEquals("User2",nicknames.get(1));
        assertEquals("User3",nicknames.get(2));
        assertEquals("User4",nicknames.get(3));
        assertEquals(4,gameController.getNumPlayerGame());
        assertTrue(gameController.isGameCreated());
        assertTrue(gameController.isGamePlaying());
        assertEquals("testGame",gameController.getGameName());
    }

    @Test
    void removePlayerTest()
    {
        gameController.removePlayer("User1");
        assertEquals(3,gameController.getNicknames().size());
        gameController.removePlayer("User2");
        assertEquals(2,gameController.getNicknames().size());
        gameController.removePlayer("User3");
        assertEquals(1,gameController.getNicknames().size());
        gameController.removePlayer("User4");
        assertEquals(0,gameController.getNicknames().size());
    }

    @Test
    void executeWhenPause() throws WrongArgumentException {

        //pauses the game then sends a command message that sets an error for the user
        List<LocatedTile> handT = new ArrayList<>();
        gameController.getGame().getPlayers().get(0).setTilesPicked(handT);
        gameController.getGame().setModelState(ModelState.PAUSE);
        SelectedTileFromHandCommandMessage finMess = new SelectedTileFromHandCommandMessage("User1", gameName, 0, ItemType.BOOK);
        CommandMessageWrapper wrapper = new CommandMessageWrapper(finMess, UserInputEvent.SELECTED_HAND_TILE);
        UserInputEvent messageType = wrapper.getType();
        CommandMessage messageCommand = wrapper.getMessage();
        gameController.setPlayerOffline("User2");
        gameController.setPlayerOffline("User3");
        gameController.setPlayerOffline("User4");
        gameController.executeCommand(messageCommand,messageType);
        assertNotNull(gameController.getGame().getErrorState("User1"));
    }
}
