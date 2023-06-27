package org.myshelfie.controller;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.myshelfie.model.Game;
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
    @Mock
    private static GameListener gameListener;
    @Mock
    private static ServerEventManager serverEventManager;
    @Mock
    private static EventManager eventManager;
    @InjectMocks
    private static LobbyController single_istance;

    //private HashMap<String,GameController> gameControllers;

    @BeforeAll
    static void beforeAll() throws IllegalAccessException {
        //MOKITO STUFF, tried to make it work for 2 hours, gave up
        //FYKI:
        //last attempt was trying to use FieldUtils.writeField to set the inner fields of the singleton
        //didn't know if it made even sense to do it, but I was desperate
        //added the dependency for that in the pom.xml
        server = Mockito.mock(Server.class);
        gameListener = Mockito.mock(GameListener.class);
        serverEventManager = Mockito.mock(ServerEventManager.class);
        eventManager = Mockito.mock(EventManager.class);
        MockitoAnnotations.openMocks(LobbyControllerTest.class);
//        try {
//            Mockito.doNothing().when(server).update(Mockito.any(), Mockito.any());
//            Mockito.doNothing().when(server).sendTo(Mockito.any(), Mockito.any());
//            Mockito.doNothing().when(gameListener).update(Mockito.any(), Mockito.any());
//            Mockito.doNothing().when(gameListener).sendLastEvent();
//            Mockito.doNothing().when(serverEventManager).sendToClients();
//            Mockito.doNothing().when(serverEventManager).notify(Mockito.any(), Mockito.any());
//            Mockito.doNothing().when(eventManager).notify(Mockito.any(), Mockito.any());
//        } catch (RemoteException e) {
//            throw new RuntimeException(e);
//        }

        single_istance = LobbyController.getInstance(server);
//        FieldUtils.writeField(single_istance, "gameListener", gameListener, true);
//        FieldUtils.writeField(single_istance, "serverEventManager", serverEventManager, true);
//        FieldUtils.writeField(single_istance, "eventManager", eventManager, true);


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
        single_istance.deleteGame("testGame");
        assertEquals(0, single_istance.getGames().size(), "There should be no games");
    }

    @BeforeEach
    void setUp(){
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void executeCommand() {

        CommandMessageWrapper cmw = new CommandMessageWrapper(null,null);
        assertThrows(NullPointerException.class, () -> {
            single_istance.executeCommand(cmw.getMessage(),cmw.getType());
        });
        CommandMessageWrapper ColMessage = new CommandMessageWrapper(new SelectedColumnMessage("User1", "testGame", 0), UserInputEvent.SELECTED_BOOKSHELF_COLUMN);
        //single_istance.executeCommand(ColMessage.getMessage(),ColMessage.getType());
    }

    @Test
    void getGameNameFromPlayerNickname() {
        assertEquals("testGame",single_istance.getGameNameFromPlayerNickname("User1"));
    }

    @Test
    void handleClientDisconnectionReconnection() {
//        single_istance.handleClientDisconnection("User1");
//        Game game = single_istance.retrieveGame("testGame");
//        assertEquals(3, game.getPlayers().size());
//        single_istance.handleClientReconnection("User1");
//        game = single_istance.retrieveGame("testGame");
//        assertEquals(4, game.getPlayers().size());
    }


}