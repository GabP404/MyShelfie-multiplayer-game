package org.myshelfie.network;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.commandMessages.UserInputEvent;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestCreateLobby {

    @BeforeAll
    public void setServerUp() {
        ClientConnectToServerTest.setServerUp();
    }


    // Parametrized test to test the logic once with RMI, the other without it
    @Test
    public void twoRMIclients() throws RemoteException, InterruptedException {
        String playerNicknameRMI1 = "RMITestNickname22";
        String playerNicknameRMI2 = "RMITestNickname23";
        String gameName = "XXServerMinecraftXX_42069_game2RMI";
        int numPlayers = 2;
        boolean isSimplifiedRules = false;

        try {
            // Try connecting to the server via RMI
            Client clientRMI = new Client(true, false);
            Client clientRMI2 = new Client(true, false);
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
    }

    @Test
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
    }

    @Test
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
    }

    @AfterAll
    public void stopServerThread() throws InterruptedException {

        ClientConnectToServerTest.stopServerThread();
    }
}
