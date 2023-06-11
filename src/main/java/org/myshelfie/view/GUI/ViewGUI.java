package org.myshelfie.view.GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.myshelfie.controller.GameController;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.messages.gameMessages.GameView;
import org.myshelfie.view.View;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

public class ViewGUI extends Application implements View  {

    private HashMap<String, String> scenes;

    private String nickname;

    private Stage stage;

    private FXMLLoader fxmlLoader;

    private GameControllerFX gameControllerFX;

    private LobbiesControllerFX lobbiesControllerFX;

    private LoginControllerFX loginControllerFX;

    private Boolean reconnecting = false;

    private static Boolean isRMI = false;

    private static String serverAddress;

    private Client client;

    public static void main(String[] args) {
        isRMI = Boolean.parseBoolean(args[0]);
        serverAddress = args[1];
        launch((String) null);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.getIcons().add(new Image(getClass().getResource("/graphics/publisher/icon.png").toString()));
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        run();
    }


    //enum sceneName {Game, EndGame, Lobbies, Login}
    public ViewGUI() {
        try {
            this.client = new Client(true, isRMI, serverAddress);
            client.connect();
            client.initializeViewGUI(this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        scenes = new HashMap<>();
        scenes.put("Game", "/fxml/GameFXML.fxml");
        scenes.put("EndGame", "/fxml/EndGameFXML.fxml");
        scenes.put("Lobbies", "fxml/LobbiesFXML.fxml");
        scenes.put("Login", "/fxml/LoginFXML.fxml");
    }

    public void setScene(String sceneName) {
        Scene scene = null;
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(scenes.get(sceneName)));
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();

        }
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("MyShelfie");
        switch (sceneName) {
            case "Login":
                loginControllerFX = fxmlLoader.getController();
                loginControllerFX.setClient(client);
                break;
            case "Game":
                gameControllerFX = fxmlLoader.getController();
                gameControllerFX.setMyNickname(this.nickname);
                break;
            //case "EndGame":
            case "Lobbies":
                lobbiesControllerFX = fxmlLoader.getController();
                break;
        }
        stage.show();
    }


    @Override
    public void update(GameView msg, GameEvent ev) {
        if(gameControllerFX != null)
            gameControllerFX.update(msg);
    }

    @Override
    public void run() {
        setScene("Login");
    }


    @Override
    public void endLoginPhase() {
        // the Client object is created during the login phase, after a name has been chosen
        this.client = loginControllerFX.getClient();
        if (reconnecting) {
             setScene("Game");
        } else {
            setScene("Lobbies");
        }
    }

    @Override
    public void endLobbyPhase() {
        setScene("Game");
    }
    @Override
    public String getGameName() {
        return null;
    }

    @Override
    public GameView getGameView() {
        return null;
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void setAvailableGames(List<GameController.GameDefinition> availableGamesList) {
        lobbiesControllerFX.updateLobbiesOptimized(availableGamesList);

    }

    @Override
    public void setReconnecting(boolean reconnecting) {
        this.reconnecting = reconnecting;
    }


    public void setClient(Client client) {
        this.client = client;
    }
}
