package org.myshelfie.controller;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.myshelfie.model.Game;
import org.myshelfie.model.ItemType;
import org.myshelfie.model.ModelState;
import org.myshelfie.network.EventManager;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.commandMessages.*;
import org.myshelfie.network.server.GameListener;
import org.myshelfie.network.server.Server;
import org.myshelfie.network.server.ServerEventManager;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class LobbyControllerTest {

    @Mock
    private static Server server;
    @InjectMocks
    private static LobbyController single_istance;


    @BeforeAll
    static void beforeAll() throws IllegalAccessException {
        server = Mockito.mock(Server.class);
        Server.eventManager = Mockito.mock(ServerEventManager.class);

        MockitoAnnotations.openMocks(LobbyControllerTest.class);
        try {
            Mockito.doNothing().when(server).update(Mockito.any(), Mockito.any());
            Mockito.doNothing().when(server).sendTo(Mockito.any(), Mockito.any());
            Mockito.doReturn(new Client()).when(server).getClient(Mockito.any());
            Mockito.doNothing().when(server).unregister(Mockito.any());
            Mockito.doNothing().when(server).log(Mockito.any(), Mockito.any());
            Mockito.doNothing().when(Server.eventManager).sendToClients();
            Mockito.doReturn(true).when(server).shouldResumeFromBackup();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        single_istance = LobbyController.getInstance(server);

        //delete the game in case it was in the backup
        try{
            single_istance.deleteGame("testGame");
            single_istance.deleteGame("anotherTestGame");
            single_istance.deleteGame("simpleGame");
        }
        catch (Exception e){
            System.out.println("Game already deleted");
        }
        single_istance.createGame(new CreateGameMessage("User1", "testGame", 4, false));
        assertEquals(Boolean.FALSE,single_istance.getGames().get(0).isFull(),"There should be only 1 player in the game");

        single_istance.joinGame(new JoinGameMessage("User2", "testGame"));
        single_istance.joinGame(new JoinGameMessage("User3", "testGame"));
        single_istance.joinGame(new JoinGameMessage("User4", "testGame"));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertEquals(Boolean.TRUE,single_istance.getGames().get(0).isFull(),"There should be 4 players in the game");

        // Retrieve the created game
        Game game = single_istance.retrieveGame("testGame");

        // Assert that the game is not null and has the expected properties
        assertNotNull(game);
        assertEquals("testGame", game.getGameName());
        assertEquals(4, game.getPlayers().size());
        assertEquals("User1", game.getPlayers().get(0).getNickname());
        assertEquals("User2", game.getPlayers().get(1).getNickname());
        assertEquals("User3", game.getPlayers().get(2).getNickname());
        assertEquals("User4", game.getPlayers().get(3).getNickname());
    }
    @AfterAll
    static void afterAll() {
        try{
            single_istance.deleteGame("testGame");
            single_istance.deleteGame("anotherTestGame");
            single_istance.deleteGame("simpleGame");
        }
        catch (Exception e){
            System.out.println("Game already deleted");
        }
        assertThrows(NullPointerException.class, () -> {
            single_istance.retrieveGame("testGame");
        });
    }

    @Test
    void NoResumeFromBackup(){
        afterAll();
        Server noResumeServer = null;
        noResumeServer = Mockito.mock(Server.class);
        MockitoAnnotations.openMocks(LobbyControllerTest.class);
        Mockito.doReturn(false).when(server).shouldResumeFromBackup();
        single_istance = null;
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        single_istance = LobbyController.getInstance(noResumeServer);
        try {
            beforeAll();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void executeCommand() {

        CommandMessageWrapper cmw = new CommandMessageWrapper(null,null);
        assertThrows(NullPointerException.class, () -> {
            single_istance.executeCommand(cmw.getMessage(),cmw.getType());
        });
        CommandMessageWrapper ColMessage = new CommandMessageWrapper(new SelectedColumnMessage("User1", "testGame", 0), UserInputEvent.SELECTED_BOOKSHELF_COLUMN);
        assertDoesNotThrow(() -> {
            single_istance.executeCommand(ColMessage.getMessage(),ColMessage.getType());
        });
    }

    @Test
    void endGameTest() {

        single_istance.retrieveGame("testGame").setModelState(ModelState.END_GAME);
        CommandMessageWrapper pickTmessage = new CommandMessageWrapper(new SelectedTileFromHandCommandMessage("User1", "testGame", 0, ItemType.CAT), UserInputEvent.SELECTED_HAND_TILE);
        assertDoesNotThrow(() -> {
            single_istance.executeCommand(pickTmessage.getMessage(),pickTmessage.getType());
        });
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertThrows(NullPointerException.class, () -> {
            single_istance.retrieveGame("testGame");
        });
    }

    @Test
    void getGameNameFromPlayerNickname() {
        assertEquals("testGame",single_istance.getGameNameFromPlayerNickname("User1"));
        assertNull(single_istance.getGameNameFromPlayerNickname("User5"));
    }

    @Test
    void handleClientDisconnectionReconnection() {

        single_istance.handleClientDisconnection("User1");
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Game game = single_istance.retrieveGame("testGame");
        assertEquals(Boolean.FALSE, game.getPlayers().get(0).isOnline());



        single_istance.handleClientReconnection("User1");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        game = single_istance.retrieveGame("testGame");
        assertEquals(Boolean.TRUE, game.getPlayers().get(0).isOnline());


        //test to recconect a client that doesn't have a game
        single_istance.createGame(new CreateGameMessage("User6", "anotherTestGame", 4, false));
        single_istance.handleClientDisconnection("User6");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        single_istance.handleClientReconnection("User6");
        assertEquals(null,single_istance.getGameNameFromPlayerNickname("User6"));

    }


    @Test
    void gameDefinitionTest() {
        //creating a game with simplified ruleset
        single_istance.createGame(new CreateGameMessage("simple1", "simpleGame", 2, true));
        assertEquals(Boolean.FALSE,single_istance.getGames().get(0).isFull(),"There should be only 1 player in the game");

        single_istance.joinGame(new JoinGameMessage("Simple2", "simpleGame"));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertTrue(single_istance.getGames().get(0).isSimplifyRules());
        assertEquals("simpleGame",single_istance.getGames().get(0).getGameName());
        assertEquals(2,single_istance.getGames().get(0).getMaxPlayers());
        List<String> nicks = single_istance.getGames().get(0).getNicknames();
        assertEquals("simple1",nicks.get(0));
        assertEquals("Simple2",nicks.get(1));

    }

}