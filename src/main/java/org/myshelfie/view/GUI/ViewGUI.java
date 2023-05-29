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
import org.myshelfie.network.messages.gameMessages.ImmutableBookshelf;
import org.myshelfie.network.messages.gameMessages.ImmutablePlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewGUI extends Application {

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
        controller.setNickname(myNickname);
        exampleGame = getExampleGame(myNickname);

        // refill the board
        try {
            exampleGame.getBoard().refillBoard(exampleGame.getPlayers().size(), exampleGame.getTileBag());
        } catch (WrongArgumentException e) {
            throw new RuntimeException(e);
        }
        // example
        List<Tile> tiles = new ArrayList<>();
        tiles.add(exampleGame.getBoard().removeTile(1, 4));
        tiles.add(exampleGame.getBoard().removeTile(1, 3));
        exampleGame.getPlayers().get(1).setTilesPicked(tiles);
        exampleGame.getPlayers().get(1).addScoringToken(new ScoringToken(4, null));
        exampleGame.getPlayers().get(1).addScoringToken(new ScoringToken(8, null));

        try {
            exampleGame.getCurrPlayer().getBookshelf().insertTile(exampleGame.getBoard().removeTile(2, 4), 1);
            exampleGame.getCurrPlayer().getBookshelf().insertTile(exampleGame.getBoard().removeTile(2, 3), 1);
            exampleGame.getCurrPlayer().getBookshelf().insertTile(exampleGame.getBoard().removeTile(2, 5), 1);

            exampleGame.getPlayers().get(2).getBookshelf().insertTile(exampleGame.getBoard().removeTile(0, 3), 3);
            exampleGame.getPlayers().get(2).getBookshelf().insertTile(exampleGame.getBoard().removeTile(0, 4), 3);

        } catch (WrongArgumentException e) {
            throw new RuntimeException(e);
        }

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

        Player player1 = new Player(creatorNickname, null);
        Player player2 = new Player("Gabriele", null);
        Player player3 = new Player("Mattia", null);
        Player player4 = new Player("Giuseppe", null);


        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);


        // leave the board empty for now
        Board board = new Board();
        TileBag tileBag = new TileBag();

        HashMap<CommonGoalCard,List<ScoringToken>> commonGoals = new HashMap<>();

        game.setupGame(players, board, commonGoals, tileBag, ModelState.WAITING_SELECTION_TILE, "PartitozzaTattica");

        return game;
    }
}