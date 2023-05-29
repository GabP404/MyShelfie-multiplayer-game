package org.myshelfie.view.GUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.myshelfie.model.Bookshelf;
import org.myshelfie.model.ScoringToken;
import org.myshelfie.model.Tile;
import org.myshelfie.model.WrongArgumentException;
import org.myshelfie.network.messages.gameMessages.GameView;
import org.myshelfie.network.messages.gameMessages.ImmutableBookshelf;
import org.myshelfie.network.messages.gameMessages.ImmutablePlayer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OtherPlayerItemController implements Initializable {
    @FXML
    private GridPane bookshelfGrid;

    @FXML
    private ImageView bookshelfImage;

    @FXML
    private ImageView finalToken;

    @FXML
    private Label nicknameLabel;

    @FXML
    private ImageView tileHand1;

    @FXML
    private ImageView tileHand2;

    @FXML
    private ImageView tileHand3;

    @FXML
    private ImageView token1;

    @FXML
    private ImageView token2;

    String nickname;


    private void updateBookshelf(ImmutableBookshelf bookshelf) {
        for (int r = 0; r < Bookshelf.NUMROWS; r++) {
            for (int c = 0; c < Bookshelf.NUMCOLUMNS; c++) {
                try {
                    if (bookshelf.getTile(r, c) != null) {
                        ImageView tileImage = (ImageView) bookshelfGrid.getChildren().get(c + r * bookshelfGrid.getColumnCount());
                        tileImage.setImage(new Image("graphics/tiles/" + bookshelf.getTile(r, c).getItemType() + "_" + bookshelf.getTile(r, c).getItemId() + ".png"));
                        tileImage.fitWidthProperty().bind(bookshelfGrid.widthProperty().divide(bookshelfGrid.getColumnConstraints().size()));
                        tileImage.fitHeightProperty().bind(bookshelfGrid.heightProperty().divide(bookshelfGrid.getRowConstraints().size()));
                        tileImage.setScaleX(0.9);
                        tileImage.setScaleY(0.9);
                        tileImage.setVisible(true);
                    } else {
                        ImageView tileImage = (ImageView) bookshelfGrid.getChildren().get(c + r * bookshelfGrid.getColumnCount());
                        tileImage.setImage(null);
                    }
                } catch (WrongArgumentException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void updateNickname(String nickname){
        this.nicknameLabel.setText(nickname);
    }

    public void updateCurrPlayer(boolean isCurrPlayer) {
        if (isCurrPlayer) {
            nicknameLabel.setTextFill(javafx.scene.paint.Color.web("#3cae1c"));
            nicknameLabel.setStyle("-fx-font-weight: bold");
        } else {
            nicknameLabel.setTextFill(javafx.scene.paint.Color.web("black"));
            nicknameLabel.setStyle("-fx-font-weight: normal");
        }
    }

    private void updateCommonGoalToken(List<ScoringToken> commonGoalTokens) {
        // TODO: check that the size is correct
        if (commonGoalTokens.size() >= 1) {
            token1.setImage(new Image("graphics/tokens/scoring_" + commonGoalTokens.get(0).getPoints() + ".jpg"));
            token1.setVisible(true);
        } else {
            token1.setVisible(false);
            token2.setVisible(false);
            return;
        }

        if (commonGoalTokens.size() >= 2) {
            token2.setImage(new Image("graphics/tokens/scoring_" + commonGoalTokens.get(1).getPoints() + ".jpg"));
            token2.setVisible(true);
            token2.setX(token1.getX() + 15);
            token2.setY(token1.getY() + 15);
        } else {
            token2.setVisible(false);
        }
    }

    private void updateFinalToken(boolean hasFinalToken) {
        // TODO: check that the size is correct
        if (hasFinalToken) {
            this.finalToken.setImage(new Image("graphics/tokens/endGame.jpg"));
            this.finalToken.setVisible(true);
        } else {
            this.finalToken.setVisible(false);
        }
    }

    private void updateTilesPicked(List<Tile> tileHand) {
        if (tileHand.size() >= 1) {
            tileHand1.setImage(new Image("graphics/tiles/" + tileHand.get(0).getItemType() + "_" + tileHand.get(0).getItemId() + ".png"));
            tileHand1.setVisible(true);
        } else {
            tileHand1.setVisible(false);
        }

        if (tileHand.size() >= 2) {
            tileHand2.setImage(new Image("graphics/tiles/" + tileHand.get(0).getItemType() + "_" + tileHand.get(1).getItemId() + ".png"));
            tileHand2.setVisible(true);
        } else {
            tileHand2.setVisible(false);
        }

        if (tileHand.size() >= 3) {
            tileHand3.setImage(new Image("graphics/tiles/" + tileHand.get(0).getItemType() + "_" + tileHand.get(2).getItemId() + ".png"));
            tileHand3.setVisible(true);
        } else {
            tileHand3.setVisible(false);
        }
    }

    /**
     * Initializes all the elements that show the player's information (nickname, bookshelf, tokens, tiles in hand etc.)
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*
        updateNickname("Player");
        updateCurrPlayer(false);
        updateBookshelf(new ImmutableBookshelf(new Bookshelf()));
        updateCommonGoalToken(new ArrayList<ScoringToken>());
        updateFinalToken(false);
        updateTilesPicked(new ArrayList<Tile>());*/
    }

    /**
     * Updates all the information about the player (except for the currPlayer flag)
     * @param thisPlayer the player to be updated
     */
    public void updatePlayersInfo(ImmutablePlayer thisPlayer) {
        updateNickname(thisPlayer.getNickname());
        updateBookshelf(thisPlayer.getBookshelf());
        updateCommonGoalToken(thisPlayer.getCommonGoalTokens());
        updateFinalToken(thisPlayer.getHasFinalToken());
        updateTilesPicked(thisPlayer.getTilesPicked());
    }
}
