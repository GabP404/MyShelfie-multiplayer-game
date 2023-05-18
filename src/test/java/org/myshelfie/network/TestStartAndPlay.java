package org.myshelfie.network;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.myshelfie.model.Bookshelf;
import org.myshelfie.model.LocatedTile;
import org.myshelfie.model.WrongArgumentException;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.commandMessages.UserInputEvent;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestStartAndPlay {
    private final String playerNickname1 = "player_1";
    private final String playerNickname2 = "player_2";
    private final String gameName = "game1";
    int numPlayers = 2;
    boolean isSimplifiedRules = false;

    Client clientRMI1;
    Client clientRMI2;

    @BeforeAll
    public void setServerUp() {
        ClientConnectToServerTest.setServerUp();
    }


    // Setup of the game for the two players using RMI
    public void twoRMICorrectSetup() throws RemoteException, InterruptedException {
        try {
            // Try connecting to the server via RMI
            clientRMI1 = new Client(true, false);
            clientRMI2 = new Client(true, false);
            assertInstanceOf(Client.class, clientRMI1);
            assertInstanceOf(Client.class, clientRMI2);
            // insert nickname
            clientRMI1.getView().setNickname(playerNickname1);
            clientRMI2.getView().setNickname(playerNickname2);
            clientRMI1.eventManager.notify(UserInputEvent.NICKNAME, playerNickname1);
            clientRMI2.eventManager.notify(UserInputEvent.NICKNAME, playerNickname2);
            assertEquals(playerNickname1, clientRMI1.getNickname());
            assertEquals(playerNickname2, clientRMI2.getNickname());

            // create game
            clientRMI1.eventManager.notify(UserInputEvent.CREATE_GAME, gameName, numPlayers, isSimplifiedRules);
            clientRMI2.eventManager.notify(UserInputEvent.JOIN_GAME, gameName);


            while (clientRMI2.getView().getGameName() == null) {
                System.out.println("RMIClient2: Waiting for game creation");
                Thread.sleep(500);
            }
            while (clientRMI1.getView().getGameName() == null) {
                System.out.println("RMIClient1: Waiting for game creation");
                Thread.sleep(500);
            }
            assertEquals(clientRMI1.getView().getGameName(), gameName);
        } catch (RemoteException e) {
            e.printStackTrace();
            fail();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            fail();
        }
    }


    /**
     * Tests some basic functionalities of the game (one turn)
     * @throws RemoteException
     * @throws InterruptedException
     */
    @Test
    public void twoRMIBasicCorrect() throws RemoteException, InterruptedException {
        try {
            // Setup of the game
            twoRMICorrectSetup();
            ///////////////////////////////////Start playing///////////////////////////////////////////////
            ////// TILES SELECTION
            // check that player_1 is the current player
            assertEquals(playerNickname1, clientRMI1.getView().getGameView().getCurrPlayer().getNickname());
            // create a list of tiles to be selected
            List<LocatedTile> tiles = new ArrayList<>();
            assertNotNull(clientRMI1.getView().getGameView().getBoard().getTile(1,3));
            assertNotNull(clientRMI1.getView().getGameView().getBoard().getTile(1,4));
            tiles.add(new LocatedTile(clientRMI1.getView().getGameView().getBoard().getTile(1,3).getItemType(), clientRMI1.getView().getGameView().getBoard().getTile(1,3).getItemId(),1,3));
            tiles.add(new LocatedTile(clientRMI1.getView().getGameView().getBoard().getTile(1,4).getItemType(), clientRMI1.getView().getGameView().getBoard().getTile(1,4).getItemId(),1,4));
            // notify the server that tiles have been selected and ready to be removed from the board
            clientRMI1.eventManager.notify(UserInputEvent.SELECTED_TILES, tiles);
            Thread.sleep(1500);
            // check that the tiles have been removed from the board and no error has been generated (on both clients)
            assertNull(clientRMI1.getView().getGameView().getBoard().getTile(1, 3));
            assertNull(clientRMI1.getView().getGameView().getBoard().getTile(1, 4));
            assertNull(clientRMI1.getView().getGameView().getErrorState(clientRMI1.getNickname()));
            assertEquals(tiles.get(0).getItemType(), clientRMI1.getView().getGameView().getCurrPlayer().getTilesPicked().get(0).getItemType());
            assertEquals(tiles.get(0).getItemId(), clientRMI1.getView().getGameView().getCurrPlayer().getTilesPicked().get(0).getItemId());
            assertEquals(tiles.get(1).getItemType(), clientRMI1.getView().getGameView().getCurrPlayer().getTilesPicked().get(1).getItemType());
            assertEquals(tiles.get(1).getItemId(), clientRMI1.getView().getGameView().getCurrPlayer().getTilesPicked().get(1).getItemId());
            assertNull(clientRMI2.getView().getGameView().getBoard().getTile(1, 3));
            assertNull(clientRMI2.getView().getGameView().getBoard().getTile(1, 4));
            assertNull(clientRMI2.getView().getGameView().getErrorState(clientRMI2.getNickname()));

            ////// BOOKSHELF'S COLUMN SELECTION
            // select the first column of the bookshelf
            int selectedColumn = 0;
            clientRMI1.eventManager.notify(UserInputEvent.SELECTED_BOOKSHELF_COLUMN, selectedColumn);
            Thread.sleep(500);
            // check that the column has been selected and no error has been generated (on both clients)
            assertEquals(clientRMI1.getNickname(), clientRMI1.getView().getGameView().getCurrPlayer().getNickname());
            assertEquals(selectedColumn, clientRMI1.getView().getGameView().getCurrPlayer().getSelectedColumn());
            assertNull(clientRMI1.getView().getGameView().getErrorState(clientRMI1.getNickname()));
            assertNull(clientRMI1.getView().getGameView().getErrorState(clientRMI2.getNickname()));
            // TODO: check if it could be useful to have the modelState inside gameView message

            ////// TILES SELECTION FROM HAND
            int selectedTileIndex;
            // select the first tile to be inserted in the bookshelf
            selectedTileIndex = 0;
            clientRMI1.eventManager.notify(UserInputEvent.SELECTED_HAND_TILE, selectedTileIndex);
            Thread.sleep(500);
            // check that the hand of the player doesn't contain the tile anymore and check that it has been inserted in the bookshelf
            // (on both clients since they both are updated with the same message)
            assertFalse(clientRMI1.getView().getGameView().getCurrPlayer().getTilesPicked().contains(tiles.get(selectedTileIndex)));
            assertFalse(clientRMI2.getView().getGameView().getCurrPlayer().getTilesPicked().contains(tiles.get(selectedTileIndex)));
            assertEquals(tiles.get(selectedTileIndex).getItemType(), clientRMI1.getView().getGameView().getCurrPlayer().getBookshelf().getTile(Bookshelf.NUMROWS - 1, selectedColumn).getItemType());
            assertEquals(tiles.get(selectedTileIndex).getItemId(), clientRMI1.getView().getGameView().getCurrPlayer().getBookshelf().getTile(Bookshelf.NUMROWS - 1, selectedColumn).getItemId());
            assertEquals(tiles.get(selectedTileIndex).getItemType(), clientRMI2.getView().getGameView().getCurrPlayer().getBookshelf().getTile(Bookshelf.NUMROWS - 1, selectedColumn).getItemType());
            assertEquals(tiles.get(selectedTileIndex).getItemId(), clientRMI2.getView().getGameView().getCurrPlayer().getBookshelf().getTile(Bookshelf.NUMROWS - 1, selectedColumn).getItemId());
            // check that the hand of the client is reduced by one tile
            assertEquals(tiles.size() - 1, clientRMI1.getView().getGameView().getCurrPlayer().getTilesPicked().size());
            assertEquals(tiles.size() - 1, clientRMI1.getView().getGameView().getCurrPlayer().getTilesPicked().size());
            // check that no error has been generated (on both clients)
            assertNull(clientRMI1.getView().getGameView().getErrorState(clientRMI1.getNickname()));
            assertNull(clientRMI1.getView().getGameView().getErrorState(clientRMI2.getNickname()));

            // select the second tile to be inserted in the bookshelf
            tiles.remove(selectedTileIndex);
            selectedTileIndex = 0;
            clientRMI1.eventManager.notify(UserInputEvent.SELECTED_HAND_TILE, selectedTileIndex);
            Thread.sleep(500);
            // NOTE: The turn is over so the current player is not player_1 anymore
            // check that the hand of the player_1 doesn't contain the tile anymore and check that it has been inserted in the bookshelf
            // (on both clients since they both are updated with the same message)
            assertFalse(clientRMI1.getView().getGameView().getPlayers().stream().filter(p -> p.getNickname().equals(clientRMI1.getNickname()))
                                                                                .findFirst()
                                                                                .get()
                                                                                .getTilesPicked()
                                                                                .contains(tiles.get(selectedTileIndex)));
            assertFalse(clientRMI2.getView().getGameView().getPlayers().stream().filter(p -> p.getNickname().equals(clientRMI1.getNickname()))
                                                                                .findFirst()
                                                                                .get()
                                                                                .getTilesPicked()
                                                                                .contains(tiles.get(selectedTileIndex)));
            assertEquals(tiles.get(selectedTileIndex).getItemId(), clientRMI1.getView().getGameView().getPlayers().stream().filter(p -> p.getNickname().equals(clientRMI1.getNickname()))
                                                                                                                    .findFirst()
                                                                                                                    .get()
                                                                                                                    .getBookshelf()
                                                                                                                    .getTile(Bookshelf.NUMROWS - 2, selectedColumn)
                                                                                                                    .getItemId());
            assertEquals(tiles.get(selectedTileIndex).getItemType(), clientRMI1.getView().getGameView().getPlayers().stream().filter(p -> p.getNickname().equals(clientRMI1.getNickname()))
                                                                                                                .findFirst()
                                                                                                                .get()
                                                                                                                .getBookshelf()
                                                                                                                .getTile(Bookshelf.NUMROWS - 2, selectedColumn)
                                                                                                                .getItemType());
            assertEquals(tiles.get(selectedTileIndex).getItemId(), clientRMI2.getView().getGameView().getPlayers().stream().filter(p -> p.getNickname().equals(clientRMI1.getNickname()))
                                                                                                                .findFirst()
                                                                                                                .get()
                                                                                                                .getBookshelf()
                                                                                                                .getTile(Bookshelf.NUMROWS - 2, selectedColumn)
                                                                                                                .getItemId());
            assertEquals(tiles.get(selectedTileIndex).getItemType(), clientRMI2.getView().getGameView().getPlayers().stream().filter(p -> p.getNickname().equals(clientRMI1.getNickname()))
                                                                                                                .findFirst()
                                                                                                                .get()
                                                                                                                .getBookshelf()
                                                                                                                .getTile(Bookshelf.NUMROWS - 2, selectedColumn)
                                                                                                                .getItemType());
            // check that no error has been generated (on both clients)
            assertNull(clientRMI1.getView().getGameView().getErrorState(clientRMI1.getNickname()));
            assertNull(clientRMI1.getView().getGameView().getErrorState(clientRMI2.getNickname()));
            assertNull(clientRMI2.getView().getGameView().getErrorState(clientRMI1.getNickname()));
            assertNull(clientRMI2.getView().getGameView().getErrorState(clientRMI2.getNickname()));

        } catch (RemoteException e) {
            e.printStackTrace();
            fail();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            fail();
        } catch (WrongArgumentException e) {
            throw new RuntimeException(e);
        }
    }


    @AfterAll
    public void stopServerThread() throws InterruptedException {

        ClientConnectToServerTest.stopServerThread();
    }
}

