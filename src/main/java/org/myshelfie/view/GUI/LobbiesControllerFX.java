package org.myshelfie.view.GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.myshelfie.controller.GameController;
import org.myshelfie.controller.LobbyController;
import org.myshelfie.model.util.Pair;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.commandMessages.UserInputEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class LobbiesControllerFX implements Initializable {

    @FXML
    private Button CreateGame_BTN;

    @FXML
    private VBox LobbyContainer;

    @FXML
    private ChoiceBox<?> Players_CB;

    @FXML
    private ChoiceBox<?> Rules_CB;

    @FXML
    private Button refresh_BTN;

    private LobbyController lobbyController;

    private HashMap<String,LobbyControllerFX> lobbiesFX;

    private Client client;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lobbiesFX = new HashMap<>();
    }



    public void createLobbies(List<GameController.GameDefinition> lobbies) {
        for(GameController.GameDefinition lobby : lobbies) {
            if (!lobby.isFull()) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("LobbyFXML.fxml"));
                try {
                    HBox lobbyHBox = fxmlLoader.load();
                    LobbyControllerFX lobbyControllerFX = fxmlLoader.getController();
                    lobbiesFX.put(lobby.getGameName(), lobbyControllerFX);
                    lobbyControllerFX.setData(lobby);
                    LobbyContainer.getChildren().add(lobbyHBox);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateLobbies(List<GameController.GameDefinition> lobbies) {
        LobbyContainer.getChildren().clear();
        createLobbies(lobbies);
    }

    public void updateLobbiesOptimized(List<GameController.GameDefinition> lobbies) {
        for (GameController.GameDefinition lobby: lobbies) {
            if (lobby.isFull() && lobbiesFX.containsKey(lobby.getGameName())) {
                LobbyContainer.getChildren().remove((lobbiesFX.get(lobby.getGameName())));
                lobbiesFX.remove(lobby.getGameName());
            } else if (!lobby.isFull() && !lobbiesFX.containsKey(lobby.getGameName())) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("LobbyFXML.fxml"));
                try {
                    HBox lobbyHBox = fxmlLoader.load();
                    LobbyControllerFX lobbyControllerFX = fxmlLoader.getController();
                    lobbiesFX.put(lobby.getGameName(), lobbyControllerFX);
                    lobbyControllerFX.setData(lobby);
                    LobbyContainer.getChildren().add(lobbyHBox);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void HandleLobbyMessage(Pair<Boolean,List<GameController.GameDefinition>> response) {
        if(response.getLeft()) {
            createLobbies(response.getRight());
        }else {
            //handle error
        }
    }

    public void refresh() {
        this.client.eventManager.notify(UserInputEvent.REFRESH_AVAILABLE_GAMES);
    }


    public void createGame() {
        this.client.eventManager.notify(UserInputEvent.CREATE_GAME, GameName.getText(), Players_CB.getValue(), Rules_CB.getValue());
    }


}
