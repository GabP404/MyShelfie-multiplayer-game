package org.myshelfie.view.GUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.myshelfie.model.Player;
import org.myshelfie.model.WrongArgumentException;
import org.myshelfie.model.util.Pair;
import org.myshelfie.network.messages.gameMessages.ImmutablePlayer;

import java.net.URL;
import java.util.ResourceBundle;

public class RankingControllerFX implements Initializable {

    @FXML
    private Label TotalPoints_LBL;

    @FXML
    private Label bookshelfPoints_LBL;

    @FXML
    private Label commonGoalPoints_LBL;

    @FXML
    private Label endToken_LBL;

    @FXML
    private Label nickname_LBL;

    @FXML
    private Label personalGoalPoints_LBL;

    @FXML
    private ImageView winner_IMG;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void setData(Pair<ImmutablePlayer,Boolean> result) {
        ImmutablePlayer player = result.getLeft();
        nickname_LBL.setText(player.getNickname());
        if(!player.isOnline()) {
            nickname_LBL.setStyle("-fx-text-fill: grey");
        }
        bookshelfPoints_LBL.setText(String.valueOf(player.getBookshelfPoints()));
        commonGoalPoints_LBL.setText(String.valueOf(player.getCommonGoalPoints()));
        personalGoalPoints_LBL.setText(String.valueOf(player.getPersonalGoalPoints()));
        endToken_LBL.setText(String.valueOf(player.getHasFinalToken()));
        TotalPoints_LBL.setText(String.valueOf(player.getTotalPoints()));
        if (result.getRight()) {
            winner_IMG.setVisible(true);
        }
    }
}
