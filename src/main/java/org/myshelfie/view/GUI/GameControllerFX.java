package org.myshelfie.view.GUI;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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

    @FXML
    private ImageView commonGoalCard1;

    @FXML
    private ImageView commonGoalCard2;

    private String nickname = null;

    private Map<String, OtherPlayerItemController> otherPlayerItemControllers;

    GameView latestGame;
    List<Tile> unconfirmedSelectedTiles;

    final int TOKEN_DIM = 50;
    final int PERSONAL_CARD_HEIGHT = 200;
    final int SELECTED_TILE_DIM = 55;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        latestGame = null;
        otherPlayerItemControllers = new HashMap<>();
        unconfirmedSelectedTiles = new ArrayList<>();

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


    ///////////////////////////// MAIN UPDATE METHOD ///////////////////////////

    /**
     * Main method to update the GUI
     * @param game
     */
    public void update(GameView game) {
        // TODO: add a GameEvent as a parameter and update only the part of the model that changed

        latestGame = game;
        unconfirmedSelectedTiles.clear();

        // Update board
        updateBoard(game.getBoard());
        // Update common goal cards
        updateCommonGoalCards(game);
        // Update other players (note that they are controlled by a different controller)
        updateOtherPlayers(game);

        // Update all MY stuff
        ImmutablePlayer me = game.getPlayers().stream().filter(p -> p.getNickname().equals(nickname)).findFirst().get();
        updateMyBookshelf(me.getBookshelf());
        updateAmICurrPlayer(game.getCurrPlayer().getNickname().equals(nickname));
        updateMyFinalToken((Boolean) game.getPlayers().stream().filter(p -> p.getNickname().equals(nickname)).findFirst().get().getHasFinalToken());
        updateMyCommonGoalToken(me.getCommonGoalTokens());
        updateMyTilesPicked(me.getTilesPicked());
        updateMyPersGoal(me.getPersonalGoal());
    }


    ///////////////////////////// ON ACTION METHODS ///////////////////////////

    /**
     * Method that is called when a tile is clicked. It's bound to the on click event of the tileImage object
     * created when the board is updated.
     * @param tileImage the ImageView object representing the tile
     * @param row the row of the tile in the board
     * @param col the column of the tile in the board
     */
    private void onTileClicked(ImageView tileImage, int row, int col) {
        // TODO: implement the messages when a wrong move is performed

        if (latestGame.getCurrPlayer().getNickname().equals(nickname)) {
            if (latestGame.getModelState() != ModelState.WAITING_SELECTION_TILE) {
                System.out.println("You can't pick a tile now!");
            } else {
                if (!unconfirmedSelectedTiles.contains(latestGame.getBoard().getTile(row, col))) {
                    if (unconfirmedSelectedTiles.size() == 3) {
                        System.out.println("You can't pick more than 3 tiles!");
                        return;
                    } else {
                        // pick the tile
                        // TODO: implement the logic that checks if the tile is valid (for the moment it's always valid)
                        unconfirmedSelectedTiles.add(latestGame.getBoard().getTile(row, col));
                        tileImage.setEffect(new DropShadow(15, Color.WHITE));
                        tileImage.setScaleX(1);
                        tileImage.setScaleY(1);
                        tileImage.toFront();
                        System.out.println("Selected tile: " + row + " " + col + " -> you've already selected " + unconfirmedSelectedTiles.size() + " tiles");
                    }

                } else {
                    // un-pick the tile
                    tileImage.setScaleX(0.9);
                    tileImage.setScaleY(0.9);
                    tileImage.setEffect(new DropShadow(5, Color.BLACK));
                    unconfirmedSelectedTiles.remove(latestGame.getBoard().getTile(row, col));
                    System.out.println("Deselected tile: " + row + " " + col);
                }

            }
        } else {
            System.out.println("It's not your turn!");
        }

    }





    /////////////////////////// VIEW UPDATE METHODS ///////////////////////////

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
            myNickname.setFont(Font.font("System", FontWeight.BOLD, 20));
            myNickname.setEffect(new DropShadow(15, Color.WHITE));
            myNickname.setVisible(true);
        } else {
            myNickname.setFont(Font.font("System", FontWeight.BOLD, 12));
            myNickname.setEffect(null);
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
     * This method updates the view of the common goal cards together with their tokens.
     * @param gameView
     */
    private void updateCommonGoalCards(GameView gameView) {
        List<CommonGoalCard> commonGoalCards = gameView.getCommonGoals();

        if (commonGoalCards.size() >= 1) {
            commonGoalCard1.setImage(new Image("graphics/commonGoalCards/" + commonGoalCards.get(0).getId() + ".jpg"));
            commonGoalCard1.setVisible(true);
            commonGoalCard2.setVisible(false);
            int k = 0;
            for (ScoringToken token : gameView.getCommonGoalTokens(commonGoalCards.get(0).getId())) {
                AnchorPane pane = (AnchorPane) commonGoalCard1.getParent();
                ImageView tokenImage = new ImageView("graphics/tokens/scoring_" + token.getPoints() + ".jpg");
                pane.getChildren().add(tokenImage);
                tokenImage.setX(commonGoalCard1.getX() + 5 * k + commonGoalCard1.getFitWidth() * 0.6);
                tokenImage.setY(commonGoalCard1.getY() + 5 * k + commonGoalCard1.getFitHeight() * 0.25);
                tokenImage.setFitWidth(40);
                tokenImage.setFitHeight(40);
                tokenImage.setVisible(true);
                tokenImage.toFront();
                k++;
            }
        }
        if (commonGoalCards.size() == 2) {
            commonGoalCard2.setImage(new Image("graphics/commonGoalCards/" + commonGoalCards.get(1).getId() + ".jpg"));
            commonGoalCard2.setVisible(true);
            int k = 0;
            for (ScoringToken token : gameView.getCommonGoalTokens(commonGoalCards.get(1).getId())) {
                AnchorPane pane = (AnchorPane) commonGoalCard2.getParent();
                ImageView tokenImage = new ImageView("graphics/tokens/scoring_" + token.getPoints() + ".jpg");
                pane.getChildren().add(tokenImage);
                tokenImage.setX(commonGoalCard2.getX() + 5 * k + commonGoalCard2.getFitWidth() * 0.6);
                tokenImage.setY(commonGoalCard2.getY() + 5 * k + commonGoalCard2.getFitHeight() * 0.25);
                tokenImage.setFitWidth(40);
                tokenImage.setFitHeight(40);
                tokenImage.setVisible(true);
                tokenImage.toFront();
                k++;
            }
        }

    }


    private void updateMyCommonGoalToken(List<ScoringToken> myCommonGoalTokens) {
        if (myCommonGoalTokens.size() >= 1) {
            myToken1.setImage(new Image("graphics/tokens/scoring_" + myCommonGoalTokens.get(0).getPoints() + ".jpg"));
            myToken1.setFitHeight(TOKEN_DIM);
            myToken1.setFitWidth(TOKEN_DIM);
            myToken1.setVisible(true);
        } else {
            myToken1.setVisible(false);
            myToken2.setVisible(false);
            return;
        }

        if (myCommonGoalTokens.size() >= 2) {
            myToken2.setImage(new Image("graphics/tokens/scoring_" + myCommonGoalTokens.get(1).getPoints() + ".jpg"));
            myToken2.setFitHeight(TOKEN_DIM);
            myToken2.setFitWidth(TOKEN_DIM);
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
            this.myFinalToken.setFitHeight(TOKEN_DIM);
            this.myFinalToken.setFitWidth(TOKEN_DIM);
            this.myFinalToken.setVisible(true);
        } else {
            this.myFinalToken.setVisible(false);
        }
    }


    private void updateMyTilesPicked(List<Tile> tileHand) {
        if (tileHand.size() >= 1) {
            myTile1.setImage(new Image("graphics/tiles/" + tileHand.get(0).getItemType() + "_" + tileHand.get(0).getItemId() + ".png"));
            myTile1.setFitHeight(SELECTED_TILE_DIM);
            myTile1.setEffect(new DropShadow(15, Color.BLACK));
            myTile1.setVisible(true);
        } else {
            myTile1.setVisible(false);
            myTile2.setVisible(false);
            myTile3.setVisible(false);
            return;
        }

        if (tileHand.size() >= 2) {
            myTile2.setImage(new Image("graphics/tiles/" + tileHand.get(0).getItemType() + "_" + tileHand.get(1).getItemId() + ".png"));
            myTile2.setFitHeight(SELECTED_TILE_DIM);
            myTile2.setEffect(new DropShadow(15, Color.BLACK));
            myTile2.setVisible(true);
        } else {
            myTile2.setVisible(false);
            myTile3.setVisible(false);
            return;
        }

        if (tileHand.size() >= 3) {
            myTile3.setImage(new Image("graphics/tiles/" + tileHand.get(0).getItemType() + "_" + tileHand.get(2).getItemId() + ".png"));
            myTile3.setFitHeight(SELECTED_TILE_DIM);
            myTile3.setEffect(new DropShadow(15, Color.BLACK));
            myTile3.setVisible(true);
        } else {
            myTile3.setVisible(false);
        }
    }

    private void updateMyPersGoal(PersonalGoalCard card) {
        myPersonalGoal.setImage(new Image("graphics/persGoalCards/Personal_Goals" + card.getId() + ".png"));
        myPersonalGoal.setFitHeight(PERSONAL_CARD_HEIGHT);
        myPersonalGoal.setEffect(new DropShadow(15, Color.BLACK));
        myPersonalGoal.setVisible(true);
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
        tileImage.setEffect(new DropShadow(5, Color.BLACK));
        tileImage.setVisible(true);

        // set the on click handler
        tileImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                onTileClicked(tileImage, row, col);
            }
        });

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
