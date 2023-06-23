package org.myshelfie.view.GUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.myshelfie.model.Player;
import org.myshelfie.model.util.Pair;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.gameMessages.GameView;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class EndGameControllerFX implements Initializable {

    @FXML
    private Button ExitGame_BTN;

    @FXML
    private Button PlayAgain_BTN;

    @FXML
    private VBox rankingContainter;

    private Client client;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void createRankingTable(GameView gameView) {
        List<Pair<Player,Boolean>> ranking = gameView.getRanking();

        for (Pair<Player,Boolean> result: ranking) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/rankingFXML.fxml"));
            try {
                HBox rankingHBox = fxmlLoader.load();
                RankingControllerFX rankingControllerFX = fxmlLoader.getController();
                rankingControllerFX.setData(result);
                rankingContainter.getChildren().add(rankingHBox);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void handlePlayAgain() {

    }

    public void handleExitGame() {
        javafx.application.Platform.exit();
    }




}
