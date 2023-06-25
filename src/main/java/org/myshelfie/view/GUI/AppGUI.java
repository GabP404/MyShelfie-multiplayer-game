package org.myshelfie.view.GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.myshelfie.model.Game;

import java.io.IOException;

public class AppGUI extends Application {

    private Stage stage;
    private FXMLLoader fxmlLoader;
    private LoginControllerFX controller;

    private Game exampleGame;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.getIcons().add(new Image(getClass().getResource("/graphics/publisher/icon.png").toString()));
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
        stage.setMinWidth(600);
        stage.setMinHeight(400);


        Scene scene;
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/LoginFXML.fxml"));
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            scene = new Scene(new Label("Error loading the scene"));
        }
        stage.setScene(scene);
        stage.setTitle("MyShelfie");
        stage.show();
        controller = fxmlLoader.getController();
    }


}