package org.myshelfie.view.GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
    private ImageView myFinalToken;

    @FXML
    private Label myNickname;

    @FXML
    private ImageView myPersonalGoal;

    @FXML
    private ImageView myTile1;

    @FXML
    private ImageView myTile2;

    @FXML
    private ImageView myTile3;

    @FXML
    private ImageView myToken1;

    @FXML
    private ImageView myToken2;

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

    public void setMyNickname(String nickname) {
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
        updateMyFinalToken((Boolean) game.getPlayers().stream().filter(p -> p.getNickname().equals(nickname)).findFirst().get().getHasFinalToken());
        updateMyCommonGoalToken(game.getPlayers().stream().filter(p -> p.getNickname().equals(nickname)).findFirst().get().getCommonGoalTokens());
        updateMyTilesPicked(game.getPlayers().stream().filter(p -> p.getNickname().equals(nickname)).findFirst().get().getTilesPicked());
        updateMyPersGoal(null);

        // TODO: myPersGoal, myTiles and myTokens

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

    private void updateMyCommonGoalToken(List<ScoringToken> commonGoalTokens) {
        if (commonGoalTokens.size() >= 1) {
            myToken1.setImage(new Image("graphics/tokens/scoring_" + commonGoalTokens.get(0).getPoints() + ".jpg"));
            myToken1.setVisible(true);
        } else {
            myToken1.setVisible(false);
            myToken2.setVisible(false);
            return;
        }

        if (commonGoalTokens.size() >= 2) {
            myToken2.setImage(new Image("graphics/tokens/scoring_" + commonGoalTokens.get(1).getPoints() + ".jpg"));
            myToken2.setVisible(true);
            myToken2.setX(myToken1.getX() + 15);
            myToken2.setY(myToken1.getY() + 15);
        } else {
            myToken2.setVisible(false);
        }
    }

    private void updateMyFinalToken(boolean hasFinalToken) {
        if (hasFinalToken) {
            this.myFinalToken.setImage(new Image("graphics/tokens/endGame.jpg"));
            this.myFinalToken.setVisible(true);
        } else {
            this.myFinalToken.setVisible(false);
        }
    }


    private void updateMyTilesPicked(List<Tile> tileHand) {
        if (tileHand.size() >= 1) {
            myTile1.setImage(new Image("graphics/tiles/" + tileHand.get(0).getItemType() + "_" + tileHand.get(0).getItemId() + ".png"));
            myTile1.setVisible(true);
        } else {
            myTile1.setVisible(false);
            myTile2.setVisible(false);
            myTile3.setVisible(false);
            return;
        }

        if (tileHand.size() >= 2) {
            myTile2.setImage(new Image("graphics/tiles/" + tileHand.get(0).getItemType() + "_" + tileHand.get(1).getItemId() + ".png"));
            myTile2.setVisible(true);
        } else {
            myTile2.setVisible(false);
            myTile3.setVisible(false);
            return;
        }

        if (tileHand.size() >= 3) {
            myTile3.setImage(new Image("graphics/tiles/" + tileHand.get(0).getItemType() + "_" + tileHand.get(2).getItemId() + ".png"));
            myTile3.setVisible(true);
        } else {
            myTile3.setVisible(false);
        }
    }

    private void updateMyPersGoal(PersonalGoalCard card) {
        // TODO: implement this
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
