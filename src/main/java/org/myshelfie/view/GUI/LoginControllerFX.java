package org.myshelfie.view.GUI;

import com.gluonhq.charm.glisten.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.client.UserInputEvent;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginControllerFX implements Initializable{
    @FXML
    private TextField nickname_LBL;
    @FXML
    private VBox nickname_VB;
    private Client client;

    /**
     * @return The client associated with this controller
     */
    Client getClient() {
        return client;
    }

    /**
     * Link this controller to the client that is using it.
     * @param client The client that is using this controller
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Check the validity of the nickname and send it to the server if it is valid,
     * by calling the {@link org.myshelfie.network.EventManager#notify notify} method of the event manager.
     * @param event The event that triggered this method
     */
    @FXML
    void sendNickname(ActionEvent event) {
        String nickname = nickname_LBL.getText();
        if (nickname.isEmpty() || !validateString(nickname) || nickname.length() >= 15) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Nickname not inserted");
            alert.showAndWait();
        }else {
            this.client.eventManager.notify(UserInputEvent.NICKNAME, nickname);
        }
    }

    /**
     * Show an error message if the nickname is already used.
     */
    void nicknameAlreadyUsed() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Nickname already used");
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nickname_VB.setManaged(true);
        nickname_VB.setVisible(true);
    }

    /**
     * Check if the string is valid, i.e. it contains only letters and numbers.
     * @param input The string to check
     * @return True if the string is valid, false otherwise
     */
    private boolean validateString(String input) {
        String regex = "^[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}