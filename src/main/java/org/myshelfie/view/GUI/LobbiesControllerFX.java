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
import org.myshelfie.model.util.Pair;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.client.UserInputEvent;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller responsible for the Lobby screen
 */
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

    /**
     * Initialization method called by JavaFX
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lobbiesFX = new HashMap<>();
        ObservableList<Integer> playerOptions = FXCollections.observableArrayList(2, 3, 4);
        Players_CB.setItems(playerOptions);
        ObservableList<String> ruleOptions = FXCollections.observableArrayList("Standard", "Simple");
        Rules_CB.setItems(ruleOptions);
    }

    /**
     * Link this controller to the client that is using it.
     * @param client The client that is using this controller
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * This method is responsible for the creation of the Lobby screen. In particular,
     * it instantiates different LobbyControllerFX objects, one for each lobby, which will handle
     * separately the display of the information of each lobby.
     * @param lobbies The list of lobbies to be displayed, in the form of {@link GameController.GameDefinition} objects
     */
    public void createLobbies(List<GameController.GameDefinition> lobbies) {
        for(GameController.GameDefinition lobby : lobbies) {
            if (!lobby.isFull()) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/LobbyFXML.fxml"));
                try {
                    HBox lobbyHBox = fxmlLoader.load();
                    lobbyHBox.maxWidthProperty().bind(LobbyContainer.widthProperty());
                    lobbyHBox.minWidthProperty().bind(LobbyContainer.widthProperty());
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

    /**
     * This method is called to update the view showing the available games.
     * @param lobbies The list of lobbies to be displayed, in the form of {@link GameController.GameDefinition} objects
     */
    public void updateLobbies(List<GameController.GameDefinition> lobbies) {
        LobbyContainer.getChildren().clear();
        createLobbies(lobbies);
    }

    /**
     * Optimized version of {@link LobbiesControllerFX#updateLobbies updateLobbies}.
     * @param lobbies The list of lobbies to be displayed, in the form of {@link GameController.GameDefinition} objects
     */
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

    /**
     * This method is called whenever the user clicks on the "Refresh" button.
     * It calls the {@link org.myshelfie.network.EventManager#notify notify} method
     * to request the updated list of available games.
     */
    public void refresh() {
        this.client.eventManager.notify(UserInputEvent.REFRESH_AVAILABLE_GAMES);
    }

    /**
     * This method is called whenever the user clicks on the "Create Game" button.
     * It validates the parameters inserted by the user and, if they are correct,
     * it calls the {@link org.myshelfie.network.EventManager#notify notify} method.
     */
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

    /**
     * This method validates a string, checking that it contains only alphanumeric characters.
     * @param input The string to be validated
     * @return True if the string is valid, false otherwise
     */
    private boolean validateString(String input) {
        String regex = "^[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

}
