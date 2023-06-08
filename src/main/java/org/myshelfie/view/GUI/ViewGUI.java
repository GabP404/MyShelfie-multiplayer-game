package org.myshelfie.view.GUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.myshelfie.controller.GameController;
import org.myshelfie.controller.LobbyController;
import org.myshelfie.model.Game;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.messages.gameMessages.GameView;
import org.myshelfie.view.View;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ViewGUI implements View {

    private HashMap<String, String> scenes;

    private String nickname;

    private Stage stage;
    private FXMLLoader fxmlLoader;

    private GameControllerFX gameControllerFX;

    private LobbiesControllerFX lobbiesControllerFX;

    private LoginControllerFX loginControllerFX;

    private Boolean reconnecting = false;



    private Client client;



    //enum sceneName {Game, EndGame, Lobbies, Login}
    public ViewGUI() {
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
        stage.show();
        switch (sceneName) {
            case "Login":
                loginControllerFX = fxmlLoader.getController();
            case "Game":
                gameControllerFX = fxmlLoader.getController();
                gameControllerFX.setMyNickname(this.nickname);
            //case "EndGame":
            case "Lobbies":
                lobbiesControllerFX = fxmlLoader.getController();
        }
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


}
