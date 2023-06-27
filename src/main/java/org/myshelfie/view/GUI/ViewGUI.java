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
import org.myshelfie.model.ModelState;
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
    private final HashMap<String, String> scenes;
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

    private Scene scene;

    public static void main(String[] args) {
        isRMI = Boolean.parseBoolean(args[0]);
        serverAddress = args[1];
        launch((String) null);
    }

    /**
     * Start the JavaFX application.
     * @param stage The primary stage
     */
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

    /**
     * Constructor for the ViewGUI class. It creates a new Client object and connects to the server.
     * Then it initializes a map containing the names of the scenes and their paths. This will be used
     * to set the scene of the stage, exploiting the {@link org.myshelfie.network.client.UserInputListener UserInputListener}
     * responses to transition between scenes, calling the {@link #setScene(String) setScene} method
     * with appropriate parameters.
     */
    public ViewGUI() {
        try {
            this.client = new Client(true, isRMI, serverAddress);
            client.connect();
            client.initializeView(this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        scenes = new HashMap<>();
        scenes.put("Game", "/fxml/GameFXML.fxml");
        scenes.put("EndGame", "/fxml/EndGameFXML.fxml");
        scenes.put("Lobbies", "/fxml/LobbiesFXML.fxml");
        scenes.put("Login", "/fxml/LoginFXML.fxml");
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(scenes.get("Login")));
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set the scene of the stage to the one specified by the sceneName parameter.
     * @param sceneName The name of the scene to set
     */
    public void setScene(String sceneName) {
        Platform.runLater(() -> {
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(scenes.get(sceneName)));
            try {
                scene.setRoot(fxmlLoader.load());
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
                    break;
                case "Lobbies":
                    lobbiesControllerFX = fxmlLoader.getController();
                    lobbiesControllerFX.setClient(client);
                    break;
            }
            stage.show();
        });
    }

    /**
     * Main method of the {@link View} interface, which allows to show the updated model
     * using information contained in the {@link GameView} object.
     * This is actually used only when the game is started, that's why it calls the
     * {@link GameControllerFX#update} method.
     * @param msg The GameView that represents the immutable version of the updated model
     * @param ev Event that caused the model's change
     */
    @Override
    public void update(GameView msg, GameEvent ev) {
        this.gameName = msg.getGameName();
        if(ev == GameEvent.GAME_END || msg.getModelState() == ModelState.END_GAME) {
            client.stopHeartbeatThread();
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
     * a {@code Platform.runLater} call.
     * You can find an example in the update method, where the {@link GameControllerFX#update} has to be called
     * after the Game scene is set.
     */
    protected static void waitForRunLater() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(semaphore::release);
        semaphore.acquire();
    }


    /**
     * Run method called at the end of the {@link #start(Stage) start} method.
     * Sets the scene to the Login one, which is actually the first one.
     */
    @Override
    public void run() {
        setScene("Login");
        // Add some music :)
        try {
            media = new Media(getClass().getResource("/audio/megalovania_lofi.mp3").toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        } catch (Exception e) {
            System.out.println("Error loading music. Nevermind, it's not that important.");
        }
    }

    /**
     * Method called by the {@link org.myshelfie.network.client.UserInputListener UserInputListener}
     * when the login phase is ended, triggering the transition to the Lobbies scene or to the
     * Game scene if the client is reconnecting.
     */
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

    /**
     * Method called by the {@link org.myshelfie.network.client.UserInputListener UserInputListener}
     * when the Lobby phase is ended, triggering the transition to the Game scene.
     */
    @Override
    public void endLobbyPhase() {
        setScene("Game");
        if (client.getNickname().toLowerCase().contains("napol")) {
            Platform.runLater(() -> {gameControllerFX.setEasterEgg("Napoli");});
            try {
                mediaPlayer.stop();
                media = new Media(getClass().getResource("/audio/sonata_quarta_corda_Bach.mp3").toExternalForm());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.play();
            } catch (Exception e) {
                System.out.println("Error loading music. Nevermind, it's not that important.");
            }
        }
    }

    /**
     * @return The name of the game that is currently being played.
     */
    @Override
    public String getGameName() {
        return gameName;
    }

    /**
     * @return The latest {@link GameView} message received.
     */
    @Override
    public GameView getGameView() {
        return null;
    }

    /**
     * Sets the nickname of the player that is using this GUI.
     * @param nickname The nickname of the player
     */
    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Setter for the list of available games. Once retrieved, this information
     * is forwarded to the {@link LobbiesControllerFX} object, which is the controller
     * responsible for the Lobby scene.
     * @param availableGamesList The list of available games
     */
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

    /**
     * Method called when the server signals that the nickname chosen by the player
     * is already used by another player. Forwards the call to the
     * {@link LoginControllerFX} object, which is the controller responsible
     * for the Login scene. Called by the {@link org.myshelfie.network.client.UserInputListener UserInputListener}.
     */
    @Override
    public void nicknameAlreadyUsed() {
        loginControllerFX.nicknameAlreadyUsed();
    }

    /**
     * Set the reconnecting status for the player using this GUI.
     * @param reconnecting True if the player is reconnecting, false otherwise
     */
    @Override
    public void setReconnecting(boolean reconnecting) {
        this.reconnecting = reconnecting;
    }
}
