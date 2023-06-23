package org.myshelfie.view.GUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.myshelfie.model.Player;
import org.myshelfie.model.util.Pair;

import java.net.URL;
import java.util.ResourceBundle;

public class RankingControllerFX implements Initializable {

    @FXML
    private Label points_LB;

    @FXML
    private Label username_LBL;

    @FXML
    private ImageView winner_IMG;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void setData(Pair<Player,Boolean> result) {
        Player player = result.getLeft();
        username_LBL.setText(player.getNickname());
        points_LB.setText(String.valueOf(player.getTotalPoints()));
        if (result.getRight()) {
            winner_IMG.setVisible(true);
        }
    }
}
