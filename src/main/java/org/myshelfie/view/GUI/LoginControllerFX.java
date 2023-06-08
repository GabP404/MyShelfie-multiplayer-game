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
    private VBox connectionMethod_VB;

    @FXML
    private Button joinGame_BTN;

    @FXML
    private Button rmi_BTN;

    @FXML
    private Button socket_BTN;

    @FXML
    private TextField username_LBL;

    @FXML
    private VBox username_VB;

    private Client client;


    public void LoginControllerFX() {

    }

    /**
     * after clicking the RMI button, this method creates a new client with RMI connection
     * @param event
     * @throws RemoteException
     */
    @FXML
    void createRmiConnection(ActionEvent event) throws RemoteException {
        client = new Client(true,true);
        changeLayout();
    }

    Client getClient() {
        return client;
    }

    /**
     * after clicking the socket button, this method creates a new client with socket connection
     * @param event
     * @throws RemoteException
     */
    @FXML
    void createSocketConnection(ActionEvent event) throws RemoteException {
        client = new Client(true,true);
    }

    /**
     * after clicking the join game button, this method sends the username to the server
     * @param event
     */
    @FXML
    void sendUsername(ActionEvent event) {
        String nickname = username_LBL.getText();
        if (nickname.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Nickname not inserted");
        }else {
            this.client.eventManager.notify(UserInputEvent.NICKNAME, nickname);
        }
    }

    /**
     * this method changes the layout of the login screen to show the username input and hide the connection method buttons
     */
    private void changeLayout() {
        connectionMethod_VB.setVisible(false);
        connectionMethod_VB.setManaged(false);
        username_VB.setVisible(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectionMethod_VB.setVisible(true);
        connectionMethod_VB.setManaged(true);
        username_VB.setVisible(false);
        username_VB.setManaged(false);
    }
}