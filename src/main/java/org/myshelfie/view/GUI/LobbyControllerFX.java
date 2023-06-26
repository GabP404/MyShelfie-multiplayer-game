package org.myshelfie.view.GUI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.myshelfie.controller.GameController;
import org.myshelfie.model.ItemType;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.client.UserInputEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class LobbyControllerFX implements Initializable{
    @FXML
    private ImageView bulletPointTile;

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
        // Choose a number from 1 to 4 using the first letter of the game name
        int seedItemType = gameDefinition.getGameName().charAt(0) % (ItemType.values().length);
        // Now choose another number in the same way, but with the last character
        int seedIndexTile = gameDefinition.getGameName().charAt(gameDefinition.getGameName().length() - 1) % 3 + 1;
        ItemType itemType = ItemType.values()[seedItemType];
        String tileName = itemType.toString() + "_" + seedIndexTile;
        bulletPointTile.setImage(new Image(getClass().getResource("/graphics/tiles/" + tileName + ".png").toString()));

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
