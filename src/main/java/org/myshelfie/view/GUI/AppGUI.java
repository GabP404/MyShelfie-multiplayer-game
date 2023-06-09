package org.myshelfie.view.GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.myshelfie.controller.Command;
import org.myshelfie.controller.WrongTurnException;
import org.myshelfie.model.*;
import org.myshelfie.network.messages.gameMessages.GameView;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppGUI extends Application {

    private Stage stage;
    private FXMLLoader fxmlLoader;
    private GameControllerFX controller;

    private Game exampleGame;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.getIcons().add(new Image(getClass().getResource("/graphics/publisher/icon.png").toString()));
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
        stage.setMinWidth(1280);
        stage.setMinHeight(720);


        Scene scene;
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/GameFXML.fxml"));
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            scene = new Scene(new Label("Error loading the scene"));
        }
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("MyShelfie");
        stage.setResizable(true);
        stage.show();
        controller = fxmlLoader.getController();


        String myNickname = "Matteo";
        controller.setMyNickname(myNickname);
        exampleGame = getExampleGame(myNickname);

        // refill the board
        try {
            exampleGame.getBoard().refillBoard(exampleGame.getPlayers().size(), exampleGame.getTileBag());
            controller.update(new GameView(exampleGame));

        } catch (WrongArgumentException e) {
            throw new RuntimeException(e);
        }

        // example
        List<Tile> tiles = new ArrayList<>();
        tiles.add(exampleGame.getBoard().removeTile(1, 4));
        tiles.add(exampleGame.getBoard().removeTile(1, 3));
        exampleGame.getPlayers().get(3).setTilesPicked(tiles);
        exampleGame.getPlayers().get(3).addScoringToken(new ScoringToken(4, null));
        exampleGame.getPlayers().get(3).addScoringToken(new ScoringToken(8, null));
        controller.update(new GameView(exampleGame));


        try {
            exampleGame.getCurrPlayer().getBookshelf().insertTile(exampleGame.getBoard().removeTile(2, 4), 1);
            exampleGame.getCurrPlayer().getBookshelf().insertTile(exampleGame.getBoard().removeTile(2, 3), 1);
            exampleGame.getCurrPlayer().getBookshelf().insertTile(exampleGame.getBoard().removeTile(2, 5), 1);
            controller.update(new GameView(exampleGame));

            exampleGame.getPlayers().get(2).getBookshelf().insertTile(exampleGame.getBoard().removeTile(0, 3), 3);
            exampleGame.getPlayers().get(2).getBookshelf().insertTile(exampleGame.getBoard().removeTile(0, 4), 3);

            exampleGame.getCurrPlayer().addTilesPicked(exampleGame.getBoard().removeTile(1, 5));
            exampleGame.getCurrPlayer().addTilesPicked(exampleGame.getBoard().removeTile(2, 6));
        } catch (WrongArgumentException e) {
            throw new RuntimeException(e);
        }

        exampleGame.getCurrPlayer().addScoringToken(new ScoringToken(4, null));
        exampleGame.getCurrPlayer().setHasFinalToken(true);

        controller.update(new GameView(exampleGame));
    }



    private void setupScene() {
        showScene("/fxml/GameFXML.fxml", () -> {
            stage.setTitle("MyShelfie");
            stage.setResizable(true);
            stage.show();
            controller = fxmlLoader.getController();
        });
    }

    private void showScene(String fxml, Command funct) {
        Platform.runLater(() -> {
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(fxml));
            Scene scene;
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                e.printStackTrace();
                scene = new Scene(new Label("Error loading the scene"));
            }
            stage.setScene(scene);
            stage.setResizable(false);
            try {
                funct.execute();
            } catch (Exception | WrongTurnException | WrongArgumentException e) {
                e.printStackTrace();
            }
        });
    }



    private Game getExampleGame(String creatorNickname) {
        Game game = new Game();
        PersonalGoalDeck personalGoalDeck;
        try {
            personalGoalDeck = PersonalGoalDeck.getInstance();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        List<PersonalGoalCard> cards = personalGoalDeck.draw(4);
        Player player1 = new Player(creatorNickname, cards.get(0));
        Player player2 = new Player("Gabriele", cards.get(1));
        Player player3 = new Player("Mattia", cards.get(2));
        Player player4 = new Player("Giuseppe", cards.get(3));

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);


        // leave the board empty for now
        Board board = new Board();
        TileBag tileBag = new TileBag();

        HashMap<CommonGoalCard,List<ScoringToken>> commonGoals = new HashMap<>();
        CommonGoalDeck commonGoalDeck = CommonGoalDeck.getInstance();
        List<CommonGoalCard> commonGoalCardsList = commonGoalDeck.drawCommonGoalCard(2);
        List<ScoringToken> tokens1 = new ArrayList<>();
        tokens1.add(new ScoringToken(4, commonGoalCardsList.get(0).getId()));
        tokens1.add(new ScoringToken(6, commonGoalCardsList.get(0).getId()));
        tokens1.add(new ScoringToken(8, commonGoalCardsList.get(0).getId()));
        List<ScoringToken> tokens2 = new ArrayList<>();
        tokens2.add(new ScoringToken(4, commonGoalCardsList.get(1).getId()));
        tokens2.add(new ScoringToken(6, commonGoalCardsList.get(1).getId()));
        tokens2.add(new ScoringToken(8, commonGoalCardsList.get(1).getId()));
        commonGoals.put(commonGoalCardsList.get(0), tokens1);
        commonGoals.put(commonGoalCardsList.get(1), tokens2);

        game.setupGame(players, board, commonGoals, tileBag, ModelState.WAITING_SELECTION_TILE, "PartitozzaTattica");

        return game;
    }
}