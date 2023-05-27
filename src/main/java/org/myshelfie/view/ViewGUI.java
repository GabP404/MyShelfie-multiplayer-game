package org.myshelfie.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.myshelfie.model.*;

import java.io.IOException;

public class ViewGUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // test the game controller by adding tiles to the board
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/GameFXML.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        GameControllerFX controller = fxmlLoader.getController();

        Board board = new Board();
        TileBag tileBag = new TileBag();
        try {
            board.refillBoard(4, tileBag);
        } catch (WrongArgumentException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < Board.DIMBOARD; i++) {
            for (int j = 0; j < Board.DIMBOARD; j++) {
                Tile tile = board.getTile(i, j);
                if (tile != null) {
                    controller.addTileToBoard(tile, i, j);
                }
            }
        }

        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch();
    }

}