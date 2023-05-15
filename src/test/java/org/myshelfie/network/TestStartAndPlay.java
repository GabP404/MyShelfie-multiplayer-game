package org.myshelfie.network;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.myshelfie.model.LocatedTile;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.commandMessages.UserInputEvent;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestStartAndPlay {


    @BeforeAll
    public void setServerUp() {
        ClientConnectToServerTest.setServerUp();
    }


    // Parametrized test to test the logic once with RMI, the other without it
    @Test
    public void twoRMIClientsPlaying() throws RemoteException, InterruptedException {
        String playerNicknameRMI1 = "player1";
        String playerNicknameRMI2 = "player2";
        String gameName = "myShelfataTattica";
        int numPlayers = 2;
        boolean isSimplifiedRules = false;

        try {
            // Try connecting to the server via RMI
            Client clientRMI1 = new Client(true, false);
            Client clientRMI2 = new Client(true, false);
            assertInstanceOf(Client.class, clientRMI1);
            assertInstanceOf(Client.class, clientRMI2);
            // insert nickname
            clientRMI1.eventManager.notify(UserInputEvent.NICKNAME, playerNicknameRMI1);
            clientRMI2.eventManager.notify(UserInputEvent.NICKNAME, playerNicknameRMI2);
            assertEquals(playerNicknameRMI1, clientRMI1.getNickname());
            assertEquals(playerNicknameRMI2, clientRMI2.getNickname());

            // create game
            clientRMI1.eventManager.notify(UserInputEvent.CREATE_GAME, gameName, numPlayers, isSimplifiedRules);
            clientRMI2.eventManager.notify(UserInputEvent.JOIN_GAME, gameName);


            while (clientRMI2.getView().getGameName() == null) {
                System.out.println("RMIClient2: Waiting for game creation");
                Thread.sleep(1000);
            }
            while (clientRMI1.getView().getGameName() == null) {
                System.out.println("RMIClient1: Waiting for game creation");
                Thread.sleep(1000);
            }
            assertEquals(clientRMI1.getView().getGameName(), gameName);


            // start game
            assertEquals(playerNicknameRMI1, clientRMI1.getView().getGameView().getCurrPlayer().getNickname());

            List<LocatedTile> tiles = new ArrayList<>();

            assertNotNull(clientRMI1.getView().getGameView().getBoard().getTile(1,3));
            assertNotNull(clientRMI1.getView().getGameView().getBoard().getTile(1,4));
            tiles.add(new LocatedTile(clientRMI1.getView().getGameView().getBoard().getTile(1,3).getItemType(),1,3));
            tiles.add(new LocatedTile(clientRMI1.getView().getGameView().getBoard().getTile(1,4).getItemType(),1,4));

            clientRMI1.eventManager.notify(UserInputEvent.SELECTED_TILES, tiles);

            while (clientRMI1.getView().getGameName() == null) {
                System.out.println("RMIClient1: Waiting for game creation");
                Thread.sleep(1000);
            }

            System.out.println(clientRMI1.getView().getGameView().getErrorState(playerNicknameRMI1));

        } catch (RemoteException e) {
            e.printStackTrace();
            fail();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            fail();
        }
    }

    /*@Test
    public void twoSocketClients() throws RemoteException, InterruptedException {
        String playerNicknameRMI1 = "SocketTestNickname1";
        String playerNicknameRMI2 = "SocketTestNickname2";
        String gameName = "XXServerMinecraftXX_42069_game2Socket";
        int numPlayers = 2;
        boolean isSimplifiedRules = false;

        try {
            // Try connecting to the server via RMI
            Client clientRMI = new Client(false, false);
            Client clientRMI2 = new Client(false, false);
            assertInstanceOf(Client.class, clientRMI);
            assertInstanceOf(Client.class, clientRMI2);
            // insert nickname
            clientRMI.eventManager.notify(UserInputEvent.NICKNAME, playerNicknameRMI1);
            clientRMI2.eventManager.notify(UserInputEvent.NICKNAME, playerNicknameRMI2);
            assertEquals(playerNicknameRMI1, clientRMI.getNickname());
            assertEquals(playerNicknameRMI2, clientRMI2.getNickname());

            // create game
            clientRMI.eventManager.notify(UserInputEvent.CREATE_GAME, gameName, numPlayers, isSimplifiedRules);
            clientRMI2.eventManager.notify(UserInputEvent.JOIN_GAME, gameName);


            while (clientRMI2.getView().getGameName() == null) {
                System.out.println("RMIClient2: Waiting for game creation");
                Thread.sleep(1000);
            }
            while (clientRMI.getView().getGameName() == null) {
                System.out.println("RMIClient1: Waiting for game creation");
                Thread.sleep(1000);
            }
            assertEquals(clientRMI.getView().getGameName(), gameName);
        } catch (RemoteException e) {
            e.printStackTrace();
            fail();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            fail();
        }
    }*/

/*    @Test
    public void oneSocketOneRMI() throws RemoteException, InterruptedException {
        String playerNicknameSocket = "SocketTestNickname";
        String playerNicknameRMI2 = "RMITestNicknameWithASocket";
        String gameName = "XXServerMinecraftXX_42069_game1Socket1RMI";
        int numPlayers = 2;
        boolean isSimplifiedRules = false;

        try {
            // Try connecting to the server via RMI
            Client clientSocket = new Client(false, false);
            Client clientRMI2 = new Client(true, false);
            assertInstanceOf(Client.class, clientSocket);
            assertInstanceOf(Client.class, clientRMI2);
            // insert nickname
            clientSocket.eventManager.notify(UserInputEvent.NICKNAME, playerNicknameSocket);
            clientRMI2.eventManager.notify(UserInputEvent.NICKNAME, playerNicknameRMI2);
            assertEquals(playerNicknameSocket, clientSocket.getNickname());
            assertEquals(playerNicknameRMI2, clientRMI2.getNickname());

            // create game
            clientSocket.eventManager.notify(UserInputEvent.CREATE_GAME, gameName, numPlayers, isSimplifiedRules);
            clientRMI2.eventManager.notify(UserInputEvent.JOIN_GAME, gameName);


            while (clientRMI2.getView().getGameName() == null) {
                System.out.println("RMIClient2: Waiting for game creation");
                Thread.sleep(1000);
            }
            while (clientSocket.getView().getGameName() == null) {
                System.out.println("RMIClient1: Waiting for game creation");
                Thread.sleep(1000);
            }
            assertEquals(clientSocket.getView().getGameName(), gameName);
        } catch (RemoteException e) {
            e.printStackTrace();
            fail();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            fail();
        }
    }*/

    @AfterAll
    public void stopServerThread() throws InterruptedException {

        ClientConnectToServerTest.stopServerThread();
    }
}

