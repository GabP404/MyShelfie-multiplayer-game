package org.myshelfie.view.GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
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
import java.util.concurrent.Semaphore;

public class ViewGUI extends Application implements View  {

    private HashMap<String, String> scenes;

    private String nickname;

    private String gameName;

    private Stage stage;

    private FXMLLoader fxmlLoader;

    private GameControllerFX gameControllerFX;

    private LobbiesControllerFX lobbiesControllerFX;

    private LoginControllerFX loginControllerFX;

    private EndGameControllerFX endGameControllerFX;

    private Boolean reconnecting = false;

    private static Boolean isRMI = false;

    private static String serverAddress;

    private Client client;

    private Media media;
    private MediaPlayer mediaPlayer;

    public static void main(String[] args) {
        isRMI = Boolean.parseBoolean(args[0]);
        serverAddress = args[1];
        launch((String) null);
    }

    @Override
    public void start(Stage stage) {
        Font.loadFont(getClass().getResource("/fonts/IndieFlower-Regular.ttf").toExternalForm(), 10);
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
        scenes.put("Lobbies", "/fxml/LobbiesFXML.fxml");
        scenes.put("Login", "/fxml/LoginFXML.fxml");
    }


    public void setScene(String sceneName) {
        Platform.runLater(() -> {
            Scene scene = null;
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(scenes.get(sceneName)));
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error loading scene " + sceneName);
                return;
            }
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMaximized(true);
            stage.setTitle("MyShelfie");
            switch (sceneName) {
                case "Login":
                    loginControllerFX = fxmlLoader.getController();
                    loginControllerFX.setClient(client);
                    break;
                case "Game":
                    stage.setMinWidth(1280);
                    stage.setMinHeight(720);
                    gameControllerFX = fxmlLoader.getController();
                    this.nickname = this.client.getNickname();
                    gameControllerFX.setMyNickname(this.nickname);
                    gameControllerFX.setClient(this.client);
                    break;
                case "EndGame":
                    endGameControllerFX = fxmlLoader.getController();
                    endGameControllerFX.setClient(client);
                    break;
                case "Lobbies":
                    lobbiesControllerFX = fxmlLoader.getController();
                    lobbiesControllerFX.setClient(client);
                    break;
            }
            stage.show();
        });
    }


//

    @Override
    public void update(GameView msg, GameEvent ev) {
        this.gameName = msg.getGameName();
        if(ev == GameEvent.GAME_END) {
            setScene("EndGame");
            Platform.runLater(() -> {
                endGameControllerFX.createRankingTable(msg);
            });
            return;
        }
        if (gameControllerFX != null) {
            gameControllerFX.update(ev, msg);
        } else {
            setScene("Game");
            // Wait for the scene to be set
            try {
                waitForRunLater();
            } catch (InterruptedException ignored) {}
            if (gameControllerFX != null) {
                gameControllerFX.update(ev, msg);
            } else {
                System.out.println("GameControllerFX is still null after setting the scene.");
            }
        }

    }

    /**
     * Util method to wait for the JavaFX thread to execute a Runnable.
     * Call this method after executing a command that is queued in the JavaFX thread with
     * a Platform.runLater call.
     *
     * You can find an example in the update method, where the gameControllerFX.update has to be called
     * after the Game scene is set.
     */
    protected static void waitForRunLater() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(semaphore::release);
        semaphore.acquire();
    }


    @Override
    public void run() {
        setScene("Login");
        // Add some music :)
//        media = new Media(getClass().getResource("/audio/megalovania_lofi.mp3").toExternalForm());
//        mediaPlayer = new MediaPlayer(media);
//        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
//        mediaPlayer.play();
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
        return gameName;
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
        Platform.runLater(() -> {
            if (lobbiesControllerFX != null) {
                lobbiesControllerFX.updateLobbies(availableGamesList);
                // lobbiesControllerFX.updateLobbiesOptimized(availableGamesList);
            } else {
                System.out.println("LobbiesControllerFX is null. Unable to set available games.");
            }
        });
    }

    @Override
    public void nicknameAlreadyUsed() {
        loginControllerFX.nicknameAlreadyUsed();
    }


    @Override
    public void setReconnecting(boolean reconnecting) {
        this.reconnecting = reconnecting;
    }


    public void setClient(Client client) {
        this.client = client;
    }
}
