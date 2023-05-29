package org.myshelfie.view.GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.myshelfie.model.*;
import org.myshelfie.network.messages.gameMessages.GameView;
import org.myshelfie.network.messages.gameMessages.ImmutableBoard;
import org.myshelfie.network.messages.gameMessages.ImmutableBookshelf;
import org.myshelfie.network.messages.gameMessages.ImmutablePlayer;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class GameControllerFX implements Initializable {
    @FXML
    private GridPane boardGrid;

    @FXML
    private ImageView boardImage;

    @FXML
    private GridPane myBookshelfGrid;

    @FXML
    private ImageView myBookshelfImage;

    @FXML
    private Label myNickname;

    @FXML
    private AnchorPane myPersonalGoalCard;

    @FXML
    private AnchorPane myTile1;

    @FXML
    private AnchorPane myTile2;

    @FXML
    private AnchorPane myTile3;

    @FXML
    private VBox otherPlayersLayout;

    private String nickname = null;

    private Map<String, OtherPlayerItemController> otherPlayerItemControllers;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        otherPlayerItemControllers = new HashMap<>();

        // set the correct size for the board
        boardGrid.prefWidthProperty().bind(boardImage.fitWidthProperty());
        boardGrid.prefHeightProperty().bind(boardImage.fitHeightProperty());

        boardGrid.setVisible(true);
        boardImage.setVisible(true);
        myBookshelfGrid.setVisible(true);
        myBookshelfImage.setVisible(true);
        myNickname.setVisible(true);
        otherPlayersLayout.setVisible(true);
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        myNickname.setText(nickname);
        myNickname.setStyle("-fx-font-weight: bold");
        myNickname.setVisible(true);
    }

    public void update(GameView game) {
        /////// UPDATE BOARD ///////
        updateBoard(game.getBoard());

        /////// UPDATE MY BOOKSHELF ///////
        updateMyBookshelf(game.getPlayers().stream().filter(p -> p.getNickname().equals(nickname)).findFirst().get().getBookshelf());
        updateAmICurrPlayer(game.getCurrPlayer().getNickname().equals(nickname));

        /////// UPDATE OTHER PLAYERS ///////
        updateOtherPlayers(game);

    }

    public void updateOtherPlayers(GameView gameView) {
        if (nickname == null)
            return;

        for (ImmutablePlayer player : gameView.getPlayers()) {
            // this is only for the other players
            if (!player.getNickname().equals(nickname)) {
                // check if a controller is already present for this player
                if (!otherPlayerItemControllers.containsKey(player.getNickname())) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/otherPlayerItem.fxml"));
                        HBox otherPlayerItem = loader.load();
                        OtherPlayerItemController controller = loader.getController();
                        // save the controller inside the map
                        otherPlayerItemControllers.put(player.getNickname(), controller);
                        // add the item to the layout
                        otherPlayersLayout.getChildren().add(otherPlayerItem);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                // update the view for this player
                OtherPlayerItemController controller = otherPlayerItemControllers.get(player.getNickname());
                controller.updatePlayersInfo(player);
                controller.updateCurrPlayer(player.getNickname().equals(gameView.getCurrPlayer().getNickname()));
            }
        }
    }


    private void updateAmICurrPlayer(boolean amICurrPlayer) {
        if (amICurrPlayer) {
            myNickname.setTextFill(javafx.scene.paint.Color.web("#3cae1c"));
            myNickname.setStyle("-fx-font-weight: bold");
            myNickname.setVisible(true);
        } else {
            myNickname.setTextFill(javafx.scene.paint.Color.web("black"));
            myNickname.setStyle("-fx-font-weight: bold");
            myNickname.setVisible(true);
        }
    }


    /**
     * This method updates the view of the board, by calling addTileToBoard for each tile.
     * @param board the board object that will be shown
     */
    private void updateBoard(ImmutableBoard board) {
        for (int r = 0; r < Board.DIMBOARD; r++) {
            for (int c = 0; c < Board.DIMBOARD; c++) {
                if (board.getTile(r, c) != null) {
                    addTileToBoard(board.getTile(r, c), r, c);
                } else {
                    removeTileFromBoard(r, c);
                }
            }
        }
    }

    private void updateMyBookshelf(ImmutableBookshelf bookshelf) {
        for (int r = 0; r < Bookshelf.NUMROWS; r++) {
            for (int c = 0; c < Bookshelf.NUMCOLUMNS; c++) {
                try {
                    if (bookshelf.getTile(r, c) != null) {
                        ImageView tileImage = (ImageView) myBookshelfGrid.getChildren().get(c + r * myBookshelfGrid.getColumnCount());
                        tileImage.setImage(new Image("graphics/tiles/" + bookshelf.getTile(r, c).getItemType() + "_" + bookshelf.getTile(r, c).getItemId() + ".png"));
                        tileImage.fitWidthProperty().bind(myBookshelfGrid.widthProperty().divide(myBookshelfGrid.getColumnConstraints().size()));
                        tileImage.fitHeightProperty().bind(myBookshelfGrid.heightProperty().divide(myBookshelfGrid.getRowConstraints().size()));
                        tileImage.setScaleX(0.9);
                        tileImage.setScaleY(0.9);
                        tileImage.setVisible(true);
                    } else {
                        ImageView tileImage = (ImageView) myBookshelfGrid.getChildren().get(c + r * myBookshelfGrid.getColumnCount());
                        tileImage.setImage(null);
                        tileImage.setVisible(false);
                    }
                } catch (WrongArgumentException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }


    /**
     * This method allows you to add a tile in the form of an ImageView to the board's gridPane.
     */
    private void addTileToBoard(Tile tile, int row, int col) {
        ImageView tileImage = (ImageView) boardGrid.getChildren().get(col + row * boardGrid.getColumnCount());
        tileImage.setImage(new Image("graphics/tiles/" + tile.getItemType() + "_" + tile.getItemId() + ".png"));
        tileImage.fitWidthProperty().bind(boardGrid.widthProperty().divide(boardGrid.getColumnConstraints().size()));
        tileImage.fitHeightProperty().bind(boardGrid.heightProperty().divide(boardGrid.getRowConstraints().size()));
        // scale down to allow a little space between tiles
        tileImage.setScaleX(0.9);
        tileImage.setScaleY(0.9);
        tileImage.setVisible(true);
    }

    /**
     * This method allows you to remove a tile from the board's representation (GridPane).
     */
    private void removeTileFromBoard(int row, int col) {
        ImageView tileImage = (ImageView) boardGrid.getChildren().get(col + row * boardGrid.getColumnCount());
        tileImage.setImage(null);
        tileImage.setVisible(false);
    }
}
