package org.myshelfie.view.GUI;

import com.gluonhq.charm.glisten.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.commandMessages.UserInputEvent;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class LoginControllerFX implements Initializable{

    public static final String LOGIN_FXML = "/org/myshelfie/view/GUI/Login.fxml";

    @FXML
    private Button endLoginPhaseButton_BTN;

    @FXML
    private TextField nickname_LBL;

    @FXML
    private VBox nickname_VB;

    private Client client;


    Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * after clicking the join game button, this method sends the username to the server
     * @param event
     */
    @FXML
    void sendNickname(ActionEvent event) {
        String nickname = nickname_LBL.getText();
        if (nickname.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Nickname not inserted");
        }else {
            this.client.eventManager.notify(UserInputEvent.NICKNAME, nickname);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nickname_VB.setManaged(true);
        nickname_VB.setVisible(true);
    }
}