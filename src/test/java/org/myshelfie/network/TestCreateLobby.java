package org.myshelfie.network;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
    public void twoRMIClients() throws RemoteException, InterruptedException {
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
            // NOTE: at the moment, this is the only way to set the nickname in the view
            //  while not interacting with the CLI
            clientRMI.getView().setNickname(playerNicknameRMI1);
            clientRMI2.getView().setNickname(playerNicknameRMI2);
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
        String playerNicknameSocket1 = "SocketTestNickname1";
        String playerNicknameSocket2 = "SocketTestNickname2";
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
            clientRMI.getView().setNickname(playerNicknameSocket1);
            clientRMI2.getView().setNickname(playerNicknameSocket2);
            clientRMI.eventManager.notify(UserInputEvent.NICKNAME, playerNicknameSocket1);
            clientRMI2.eventManager.notify(UserInputEvent.NICKNAME, playerNicknameSocket2);
            assertEquals(playerNicknameSocket1, clientRMI.getNickname());
            assertEquals(playerNicknameSocket2, clientRMI2.getNickname());

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
        String playerNicknameRMI = "RMITestNicknameWithASocket";
        String gameName = "XXServerMinecraftXX_42069_game1Socket1RMI";
        int numPlayers = 2;
        boolean isSimplifiedRules = false;

        try {
            // Try connecting to the server via RMI
            Client clientSocket = new Client(false, false);
            Client clientRMI = new Client(true, false);
            assertInstanceOf(Client.class, clientSocket);
            assertInstanceOf(Client.class, clientRMI);
            // insert nickname
            clientSocket.getView().setNickname(playerNicknameSocket);
            clientRMI.getView().setNickname(playerNicknameRMI);
            clientSocket.eventManager.notify(UserInputEvent.NICKNAME, playerNicknameSocket);
            clientRMI.eventManager.notify(UserInputEvent.NICKNAME, playerNicknameRMI);
            assertEquals(playerNicknameSocket, clientSocket.getNickname());
            assertEquals(playerNicknameRMI, clientRMI.getNickname());

            // create game
            clientSocket.eventManager.notify(UserInputEvent.CREATE_GAME, gameName, numPlayers, isSimplifiedRules);
            clientRMI.eventManager.notify(UserInputEvent.JOIN_GAME, gameName);


            while (clientRMI.getView().getGameName() == null) {
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
