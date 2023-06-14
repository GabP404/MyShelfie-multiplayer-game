package org.myshelfie.view.GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LobbiesControllerFX implements Initializable {

    @FXML
    private Button CreateGame_BTN;

    @FXML
    private VBox LobbyContainer;

    @FXML
    private ChoiceBox<Integer> Players_CB;

    @FXML
    private ChoiceBox<String> Rules_CB;

    @FXML
    private Button refresh_BTN;

    @FXML
    private TextField CreateGameName_TXT;


    private HashMap<String,LobbyControllerFX> lobbiesFX;

    private Client client;

    private String gameName;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lobbiesFX = new HashMap<>();
        ObservableList<Integer> playerOptions = FXCollections.observableArrayList(2, 3, 4);
        Players_CB.setItems(playerOptions);
        ObservableList<String> ruleOptions = FXCollections.observableArrayList("Standard", "Simple");
        Rules_CB.setItems(ruleOptions);
    }


    public void setClient(Client client) {
        this.client = client;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void createLobbies(List<GameController.GameDefinition> lobbies) {
        for(GameController.GameDefinition lobby : lobbies) {
            if (!lobby.isFull()) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/LobbyFXML.fxml"));
                try {
                    HBox lobbyHBox = fxmlLoader.load();
                    LobbyControllerFX lobbyControllerFX = fxmlLoader.getController();
                    lobbyControllerFX.setClient(this.client);
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
                fxmlLoader.setLocation(getClass().getResource("/fxml/LobbyFXML.fxml"));
                try {
                    HBox lobbyHBox = fxmlLoader.load();
                    LobbyControllerFX lobbyControllerFX = fxmlLoader.getController();
                    lobbyControllerFX.setClient(this.client);
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
        if(CreateGameName_TXT.getText().isEmpty() || !validateString(CreateGameName_TXT.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Please enter a game name");
            alert.showAndWait();
            return;
        }
        if(Players_CB.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Please select a number of players");
            alert.showAndWait();
            return;
        }
        if(Rules_CB.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Please select a rule set");
            alert.showAndWait();
            return;
        }
        this.client.eventManager.notify(UserInputEvent.CREATE_GAME, CreateGameName_TXT.getText(), Players_CB.getValue(), Rules_CB.getValue().equals("Simple"));
    }

    private boolean validateString(String input) {
        String regex = "^[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

}
