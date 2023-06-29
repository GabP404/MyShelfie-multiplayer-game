package org.myshelfie.view.GUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.myshelfie.model.Bookshelf;
import org.myshelfie.model.ScoringToken;
import org.myshelfie.model.Tile;
import org.myshelfie.model.WrongArgumentException;
import org.myshelfie.network.messages.gameMessages.ImmutableBookshelf;
import org.myshelfie.network.messages.gameMessages.ImmutablePlayer;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * This class controls the view of other players' public items,
 * which are their bookshelf, their nickname, their hand and their tokens.
 * Thus, all the methods refers to a player which will be different from the
 * one referred as "me" in the main class {@link GameControllerFX}.
 */
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
    final int TOKEN_DIM = 45;

    /**
     * Updates the player's bookshelf
     * @param bookshelf the bookshelf to be updated
     */
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

    /**
     * Updates the player's nickname
     * @param nickname The nickname of the player
     */
    public void updateNickname(String nickname){
        this.nickname = nickname;
        this.nicknameLabel.setText(nickname);
    }

    /**
     * Updates the player name's style to show if it's the current player
     * @param isCurrPlayer True if the player is the current player, false otherwise
     */
    public void updateCurrPlayer(boolean isCurrPlayer) {
        if (isCurrPlayer) {
            nicknameLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
            nicknameLabel.setEffect(new DropShadow(10, Color.WHITE));
            nicknameLabel.setText(nickname + " \uD83C\uDFF3\uFE0F");
            nicknameLabel.setVisible(true);
        } else {
            nicknameLabel.setFont(Font.font("System", FontWeight.NORMAL, 10));
            nicknameLabel.setEffect(null);
            nicknameLabel.setText(nickname);
            nicknameLabel.setVisible(true);
        }

    }

    /**
     * Updates the player's common goal tokens
     * @param commonGoalTokens The list of common goal tokens
     */
    private void updateCommonGoalToken(List<ScoringToken> commonGoalTokens) {
        if (commonGoalTokens.size() >= 1) {
            token1.setImage(new Image("graphics/tokens/scoring_" + commonGoalTokens.get(0).getPoints() + ".jpg"));
            token1.setVisible(true);
            token1.setLayoutX(2);
            token1.setLayoutY(10);
            token1.setFitHeight(TOKEN_DIM);
            token1.setFitWidth(TOKEN_DIM);
        } else {
            token1.setVisible(false);
            token2.setVisible(false);
            return;
        }

        if (commonGoalTokens.size() >= 2) {
            token2.setImage(new Image("graphics/tokens/scoring_" + commonGoalTokens.get(1).getPoints() + ".jpg"));
            token2.setVisible(true);
            token2.setLayoutX(32);
            token2.setLayoutY(-4);
            token2.toFront();
            token2.setFitHeight(TOKEN_DIM);
            token2.setFitWidth(TOKEN_DIM);
        } else {
            token2.setVisible(false);
        }
    }

    /**
     * Updates the view of the final token if the player has it.
     * @param hasFinalToken True if the player has the final token, false otherwise
     */
    private void updateFinalToken(boolean hasFinalToken) {
        if (hasFinalToken) {
            this.finalToken.setImage(new Image("graphics/tokens/endGame.jpg"));
            finalToken.setFitHeight(TOKEN_DIM);
            finalToken.setFitWidth(TOKEN_DIM);
            this.finalToken.setVisible(true);
        } else {
            this.finalToken.setVisible(false);
        }
    }

    /**
     * Updates the player's hand
     * @param tileHand The list of tiles in the player's hand
     */
    private void updateTilesPicked(List<Tile> tileHand) {
        if (tileHand.size() >= 1) {
            tileHand1.setImage(new Image("graphics/tiles/" + tileHand.get(0).getItemType() + "_" + tileHand.get(0).getItemId() + ".png"));
            tileHand1.setVisible(true);
        } else {
            tileHand1.setVisible(false);
        }

        if (tileHand.size() >= 2) {
            tileHand2.setImage(new Image("graphics/tiles/" + tileHand.get(1).getItemType() + "_" + tileHand.get(1).getItemId() + ".png"));
            tileHand2.setVisible(true);
        } else {
            tileHand2.setVisible(false);
        }

        if (tileHand.size() >= 3) {
            tileHand3.setImage(new Image("graphics/tiles/" + tileHand.get(2).getItemType() + "_" + tileHand.get(2).getItemId() + ".png"));
            tileHand3.setVisible(true);
        } else {
            tileHand3.setVisible(false);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * Updates all the information about the player (except for the currPlayer flag)
     * @param thisPlayer the player to be updated
     */
    public void updatePlayersInfo(ImmutablePlayer thisPlayer) {
        updateBookshelf(thisPlayer.getBookshelf());
        updateCommonGoalToken(thisPlayer.getCommonGoalTokens());
        updateFinalToken(thisPlayer.getHasFinalToken());
        updateTilesPicked(thisPlayer.getTilesPicked());
        updateNickname(thisPlayer.getNickname());

        // Gray out the player if he is offline
        if (!thisPlayer.isOnline()) {
            ColorAdjust desaturation = new ColorAdjust();
            desaturation.setSaturation(-1);
            setEveryItemSaturation(desaturation);
        } else {
            setEveryItemSaturation(null);
        }
    }

    /**
     * Sets the saturation of every item in the player's view. This is used to
     * gray out the player if he/she is offline.
     * @param desaturation The desaturation to be applied
     */
    private void setEveryItemSaturation(ColorAdjust desaturation) {
        bookshelfGrid.setEffect(desaturation);
        bookshelfImage.setEffect(desaturation);
        nicknameLabel.setEffect(desaturation);
        tileHand1.setEffect(desaturation);
        tileHand2.setEffect(desaturation);
        tileHand3.setEffect(desaturation);
        token1.setEffect(desaturation);
        token2.setEffect(desaturation);
        finalToken.setEffect(desaturation);
    }
}
