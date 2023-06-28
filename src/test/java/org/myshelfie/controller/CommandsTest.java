package org.myshelfie.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.myshelfie.model.*;
import org.myshelfie.network.messages.commandMessages.CreateGameMessage;
import org.myshelfie.network.messages.commandMessages.JoinGameMessage;
import org.myshelfie.network.messages.commandMessages.PickedTilesCommandMessage;
import org.myshelfie.network.messages.commandMessages.SelectedTileFromHandCommandMessage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This class contains the tests for all the classes that represents a command
 * used from the client to request an action to the server.
 */
public class CommandsTest {
    static Game game;
    static List<Player> players;
    static CommonGoalDeck commonGoalDeck = CommonGoalDeck.getInstance();
    static PersonalGoalDeck personalGoalDeck;

    static {
        try {
            personalGoalDeck = PersonalGoalDeck.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    static List<PersonalGoalCard> personalGoalCardList = personalGoalDeck.draw(2);

    static List<CommonGoalCard> commonGoalCardList = commonGoalDeck.drawCommonGoalCard(4);
    static HashMap<CommonGoalCard,List<ScoringToken>> commonGoal = new HashMap<>();
    static TileBag tb = new TileBag();

    public CommandsTest() throws IOException, URISyntaxException {
    }

    @BeforeEach
    public void setupGame() throws WrongArgumentException {
        players = new ArrayList<>();
        players.add(new Player("User0", personalGoalCardList.get(0)));
        players.add(new Player("User1", personalGoalCardList.get(1)));
        for (CommonGoalCard x : commonGoalCardList) {
            commonGoal.put(x, (List<ScoringToken>) GameTest.createTokensPersonalGoalCard(x.getId(), 2));
        }
        game = new Game();
        game.setupGame(players, new Board(2),commonGoal,tb,ModelState.WAITING_SELECTION_TILE, "testGame");
        game.getBoard().refillBoard(game.getPlayers().size(), game.getTileBag());
    }

    @Test
    public void testPickTilesCommand() throws WrongArgumentException {
        List<LocatedTile> tiles = new ArrayList<>();
        tiles.add(new LocatedTile(ItemType.BOOK,4,1));
        tiles.add(new LocatedTile(ItemType.CAT,5,1));

        // Select and execute the tiles from the board
        PickTilesCommand pt = new PickTilesCommand(game.getBoard(), players.get(0), new PickedTilesCommandMessage("User0", "testGame", tiles), ModelState.WAITING_SELECTION_TILE);
        assertDoesNotThrow(pt::execute);

        // Check with tiles not in the board
        tiles.clear();
        tiles.add(new LocatedTile(ItemType.BOOK,4,1));
        assertThrows(WrongArgumentException.class,
                () -> new PickTilesCommand(game.getBoard(), players.get(0), new PickedTilesCommandMessage("User0", "testGame", tiles), ModelState.WAITING_SELECTION_TILE));

        // Check with more than 3 tiles
        tiles.clear();
        tiles.add(new LocatedTile(ItemType.BOOK,3,3));
        tiles.add(new LocatedTile(ItemType.CAT,4,3));
        tiles.add(new LocatedTile(ItemType.CAT,5,3));
        tiles.add(new LocatedTile(ItemType.CAT,6,3));
        pt = new PickTilesCommand(game.getBoard(), players.get(0), new PickedTilesCommandMessage("User0", "testGame", tiles), ModelState.WAITING_SELECTION_TILE);
        assertThrows(WrongArgumentException.class, pt::execute);
        // Check with wrong model state
        pt = new PickTilesCommand(game.getBoard(), players.get(0), new PickedTilesCommandMessage("User0", "testGame", tiles), ModelState.WAITING_SELECTION_BOOKSHELF_COLUMN);
        assertThrows(InvalidCommand.class, pt::execute);

        // Check with two non-adjacent tiles
        tiles.clear();
        tiles.add(new LocatedTile(ItemType.CAT,3,2));
        tiles.add(new LocatedTile(ItemType.CAT,5,2));
        pt = new PickTilesCommand(game.getBoard(), players.get(0), new PickedTilesCommandMessage("User0", "testGame", tiles), ModelState.WAITING_SELECTION_TILE);
        assertThrows(WrongArgumentException.class, pt::execute);
    }

    @Test
    public void testSelectTileFromHandCommand() throws WrongArgumentException {
        List<LocatedTile> tilesPicked = Arrays.asList(
                new LocatedTile(ItemType.BOOK, 1, 4, 1),
                new LocatedTile(ItemType.CAT, 2, 5, 1),
                null
        );
        players.get(0).setTilesPicked(tilesPicked);
        players.get(0).setSelectedColumn(0);
        // Select and execute the tile from the hand
        SelectTileFromHandCommand st = new SelectTileFromHandCommand(players.get(0), new SelectedTileFromHandCommandMessage("User0", "testGame", 0, ItemType.BOOK), ModelState.WAITING_3_SELECTION_TILE_FROM_HAND);
        assertDoesNotThrow(st::execute);

        // Check with wrong nickname
        st = new SelectTileFromHandCommand(players.get(0), new SelectedTileFromHandCommandMessage("User1", "testGame", 0, ItemType.BOOK), ModelState.WAITING_2_SELECTION_TILE_FROM_HAND);
        assertThrows(WrongTurnException.class, st::execute);

        // Check with wrong model status
        st = new SelectTileFromHandCommand(players.get(0), new SelectedTileFromHandCommandMessage("User0", "testGame", 0, ItemType.BOOK), ModelState.WAITING_SELECTION_BOOKSHELF_COLUMN);
        assertThrows(InvalidCommand.class, st::execute);

        // Check with null tile
        st = new SelectTileFromHandCommand(players.get(0), new SelectedTileFromHandCommandMessage("User0", "testGame", 1, ItemType.BOOK), ModelState.WAITING_1_SELECTION_TILE_FROM_HAND);
        assertThrows(WrongArgumentException.class, st::execute);
    }

    @Test
    public void testJoinGameCommand() throws IOException, URISyntaxException {
        HashMap<String,GameController> gameControllers = new HashMap<>();
        gameControllers.put("testGame", new GameController("testGame", 2, 2));

        JoinGameCommand jg = new JoinGameCommand(gameControllers, new JoinGameMessage("nickname", "testGame"));
        assertDoesNotThrow(jg::execute);

        //Try with a non-existing game
        jg = new JoinGameCommand(gameControllers, new JoinGameMessage("nickname", "testGameNonExisting"));
        assertThrows(IllegalArgumentException.class, jg::execute);

        //Try with a full game
        gameControllers.get("testGame").setupGame();
        jg = new JoinGameCommand(gameControllers, new JoinGameMessage("nickname", "testGame"));
        assertThrows(IllegalArgumentException.class, jg::execute);
    }

    @Test
    public void createGameCommand() throws WrongArgumentException, IOException, URISyntaxException {
        HashMap<String,GameController> gameControllers = new HashMap<>();
        CreateGameCommand cg = new CreateGameCommand(gameControllers, new CreateGameMessage("nickname", "testGame", 2, false));
        assertDoesNotThrow(cg::execute);

        // Try to create another game with the same name
        cg = new CreateGameCommand(gameControllers, new CreateGameMessage("newNickname", "testGame", 2, false));
        assertThrows(IllegalArgumentException.class, cg::execute);
    }
}
