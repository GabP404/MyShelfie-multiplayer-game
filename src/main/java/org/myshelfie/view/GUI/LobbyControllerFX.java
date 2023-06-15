package org.myshelfie.view.GUI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.myshelfie.controller.GameController;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.commandMessages.UserInputEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class LobbyControllerFX implements Initializable{

    @FXML
    private Label GameNameJoin_LB;

    @FXML
    private Button JoinGame_BTN;

    @FXML
    private Label MaxNumPlayers_LB;

    @FXML
    private Label NumPlayersConnected_LB;

    @FXML
    private Label Rules_LB;

    private Client client;

    public void setData(GameController.GameDefinition gameDefinition) {
        GameNameJoin_LB.setText(gameDefinition.getGameName());
        MaxNumPlayers_LB.setText(gameDefinition.getMaxPlayers() + "");
        NumPlayersConnected_LB.setText(gameDefinition.getNicknames().size() + "");
        if(gameDefinition.isSimplifyRules()) {
            Rules_LB.setText("Simple");
        } else {
            Rules_LB.setText("Standard");
        }
        if (this.client == null) {
            System.out.println("UANMUANMUANM CLIENT IS NULL UAUA");
        }
        JoinGame_BTN.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                LobbyControllerFX.this.client.eventManager.notify(UserInputEvent.JOIN_GAME, gameDefinition.getGameName());
            }
        });
    }

    public void setClient(Client client) {
        this.client = client;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void LobbyControllerFX() {

    }


}
