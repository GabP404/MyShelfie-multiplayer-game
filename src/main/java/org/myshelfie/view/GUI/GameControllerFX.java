package org.myshelfie.view.GUI;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import org.myshelfie.model.*;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.commandMessages.UserInputEvent;
import org.myshelfie.network.messages.gameMessages.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GameControllerFX implements Initializable {
    @FXML
    private GridPane boardGrid;

    @FXML
    private ImageView boardImage;

    @FXML
    private GridPane colSelectionArrowsGrid;

    @FXML
    private ImageView commonGoalCard1;

    @FXML
    private ImageView commonGoalCard2;

    @FXML
    private GridPane myBookshelfGrid;

    @FXML
    private ImageView myBookshelfImage;

    @FXML
    private AnchorPane myBookshelfPane;

    @FXML
    private ImageView myFinalToken;

    @FXML
    private Label myNickname;

    @FXML
    private ImageView myPersonalGoal;

    @FXML
    private ImageView myToken1;

    @FXML
    private ImageView myToken2;

    @FXML
    private VBox otherPlayersLayout;

    @FXML
    private Button tilesConfirmButton;

    @FXML
    private GridPane tilesHandGrid;

    private boolean firstSetupDone = false;

    private String nickname = null;

    private Map<String, OtherPlayerItemController> otherPlayerItemControllers;

    GameView latestGame;
    List<LocatedTile> unconfirmedSelectedTiles;
    private int selectedColumn = -1;

    final int TOKEN_DIM = 50;
    final int PERSONAL_CARD_HEIGHT = 200;
    final int SELECTED_TILE_DIM = 55;
    final int SEL_COL_ARROW_WIDTH = 50;
    final double TILE_DIM = 45;

    private Client client;


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
        colSelectionArrowsGrid.setVisible(true);
    }

    public void setMyNickname(String nickname) {
        this.nickname = nickname;
        myNickname.setText(nickname);
        myNickname.setStyle("-fx-font-weight: bold");
        myNickname.setVisible(true);
    }

    public void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Message:");
        alert.setContentText(message);

        alert.showAndWait();
    }


    ///////////////////////////// MAIN UPDATE METHOD ///////////////////////////

    /**
     * Main method to update the GUI
     * @param game
     */
    public void update(GameEvent ev, GameView game) {
        // TODO: check that all the GameEvents are covered
        latestGame = game;
        System.out.println("STATUS: "+ game.getModelState());
        ImmutablePlayer me = game.getPlayers().stream().filter(p -> p.getNickname().equals(nickname)).findFirst().get();

        if (ev != GameEvent.PLAYER_ONLINE_UPDATE) {
            unconfirmedSelectedTiles.clear();
        }

        switch (ev) {
            case BOARD_UPDATE -> {
                if (!firstSetupDone) {
                    updateEverything(game);
                    firstSetupDone = true;
                    return;
                }
                // Update board
                updateBoard(game.getBoard());
                updateMyBookshelf(me.getBookshelf());
                udpateColSelectionArrows();
            }
            case TILES_PICKED_UPDATE -> {
                updateBoard(game.getBoard());
            }
            case SELECTED_COLUMN_UPDATE -> {
                udpateColSelectionArrows();
            }
            case TOKEN_STACK_UPDATE -> {
                // Update common goal cards
                updateCommonGoalCards(game);
            }
            case CURR_PLAYER_UPDATE -> {
                updateAmICurrPlayer(game.getCurrPlayer().getNickname().equals(nickname));
            }
            case FINAL_TOKEN_UPDATE -> {
                updateMyFinalToken((Boolean) game.getPlayers().stream().filter(p -> p.getNickname().equals(nickname)).findFirst().get().getHasFinalToken());
            }
            case ERROR -> {
                updateEverything(game);
                return;
            }
            default -> {
                System.out.println("Entering the default updates...");
                updateEverything(game);
                return;
            }
        }
        // Actions that are performed on every update
        updateTilesConfirmButton();
        updateMyBookshelf(me.getBookshelf());
        updateMyPersGoal(me.getPersonalGoal());
        updateMyTilesPicked(me.getTilesPicked());
        // Update other players (note that they are controlled by a different controller)
        updateOtherPlayers(game);
    }

    private void updateEverything(GameView game) {
        // Update board
        updateBoard(game.getBoard());
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
        udpateColSelectionArrows();
        updateTilesConfirmButton();
        // Update common goal cards
        updateCommonGoalCards(game);
    }


    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////// ON ACTION METHODS ///////////////////////////
    ///////////////////////////////////////////////////////////////////////////


    /**
     * Method that is called when a tile is clicked. It's bound to the on click event of the tileImage object
     * created when the board is updated.
     * @param tileImage the ImageView object representing the tile
     * @param row the row of the tile in the board
     * @param col the column of the tile in the board
     */
    private void onTileClicked(ImageView tileImage, int row, int col) {
        if (latestGame.getCurrPlayer().getNickname().equals(nickname)) {
            if (latestGame.getModelState() != ModelState.WAITING_SELECTION_TILE) {

                showErrorDialog("You can't pick a tile now!");
            } else {
                LocatedTile t = new LocatedTile(latestGame.getBoard().getTile(row, col).getItemType(), latestGame.getBoard().getTile(row, col).getItemId(), row, col);
                if (!unconfirmedSelectedTiles.contains(t)) {
                    if (unconfirmedSelectedTiles.size() == 3) {
                        showErrorDialog("You can't pick more than 3 tiles!");
                        return;
                    } else {
                        // pick the tile
                        unconfirmedSelectedTiles.add(t);
                        if (!isTilesGroupSelectable(latestGame.getBoard(), unconfirmedSelectedTiles)) {
                            unconfirmedSelectedTiles.remove(t);
                            showErrorDialog("You can't pick this tile!");
                            return;
                        }
                        tileImage.setEffect(new DropShadow(15, Color.GREEN));
                        tileImage.toFront();

                        // Create a ScaleTransition with desired properties
                        double finalScale = 1.2; // The final scale value
                        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), tileImage);
                        scaleTransition.setToX(finalScale * tileImage.getScaleX());
                        scaleTransition.setToY(finalScale * tileImage.getScaleY());
                        scaleTransition.setAutoReverse(false);
                        // Set the final scale values directly at the end of the animation
                        scaleTransition.setOnFinished(event -> {
                            System.out.println("Selected tile: " + row + " " + col + " -> you've already selected " + unconfirmedSelectedTiles.size() + " tiles");
                        });

                        // Play the animation
                        scaleTransition.play();
                    }
                } else {
                    // un-pick the tile
                    tileImage.setScaleX(0.9);
                    tileImage.setScaleY(0.9);
                    tileImage.setEffect(new DropShadow(5, Color.BLACK));
                    unconfirmedSelectedTiles.remove(t);
                    System.out.println("Deselected tile: " + row + " " + col);
                }
            }
        } else {
            showErrorDialog("It's not your turn!");
        }
    }

    private boolean isTilesGroupSelectable(ImmutableBoard board, List<LocatedTile> unconfirmedSelectedTiles) {
        boolean invalid = unconfirmedSelectedTiles.stream().map(
                t -> {
                    int row = t.getRow();
                    int col = t.getCol();
                    return board.getTile(row, col) != null && board.hasOneOrMoreFreeBorders(row, col);
                }
        ).anyMatch(Predicate.isEqual(false));
        if (invalid)
            return false;

        // Skip the check if there is only one tile in the selection
        if (unconfirmedSelectedTiles.size() < 2) {
            // If so, return true since a single tile or no tiles are always in a line
            return true;
        }

        // The tiles are horizontal / vertical if all the rows / cols are the same
        boolean isHorizontal = unconfirmedSelectedTiles.stream().map(LocatedTile::getRow).distinct().count() == 1;
        boolean isVertical = unconfirmedSelectedTiles.stream().map(LocatedTile::getCol).distinct().count() == 1;

        if (!isHorizontal && !isVertical)
            return false;

        // Check that the chosen tile are "sequential" i.e., adjacent to each other
        SortedSet<Integer> sortedIndexes = new TreeSet<>();
        if (isHorizontal)
            sortedIndexes.addAll(unconfirmedSelectedTiles.stream().map(LocatedTile::getCol).collect(Collectors.toSet()));
        if (isVertical)
            sortedIndexes.addAll(unconfirmedSelectedTiles.stream().map(LocatedTile::getRow).collect(Collectors.toSet()));

        return sortedIndexes.last() - sortedIndexes.first() == sortedIndexes.size() - 1;
    }


    private void onArrowClicked(int column, ImageView arrowImage) {
        // TODO: implement controls that prevent the player from selecting a column that cannot contains the tiles he selected
        selectedColumn = column;
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), arrowImage);
        scaleTransition.setToX(arrowImage.getScaleX() * 1.1);
        scaleTransition.setToY(arrowImage.getScaleY() * 1.1);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);
        // Set the notify call to be sent after the animation is finished
        scaleTransition.setOnFinished(event -> {
            System.out.println("Selected column: " + column);
            this.client.eventManager.notify(UserInputEvent.SELECTED_BOOKSHELF_COLUMN, column);
        });

        // Play the animation
        scaleTransition.play();
    }


    private void onConfirmTilesSelection() {
        // TODO: implement controls that prevent the player from selecting to many tiles when he has
        //       not enough space in at least one of the columns of the bookshelf
        if (unconfirmedSelectedTiles.size() >= 1) {
            Platform.runLater(() -> {
                // Remove the selected tiles from the board
                for (LocatedTile t : unconfirmedSelectedTiles) {
                    int row = t.getRow();
                    int col = t.getCol();
                    boardGrid.getChildren().stream().filter(node -> GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col).forEach(node -> {
                        Platform.runLater(() -> {
                            boardGrid.getChildren().remove(node);
                        });
                    });
                }
            });

            System.out.println("Sending to server " + unconfirmedSelectedTiles.size() + " tiles");
            this.client.eventManager.notify(UserInputEvent.SELECTED_TILES, unconfirmedSelectedTiles);
            tilesConfirmButton.setVisible(false);
        } else {
            showErrorDialog("You must select at least one tile!");
        }
    }

    private void onTileFromHandClicked(ImageView tileImage, int tileIndex) {
        if (tileIndex >= latestGame.getPlayers().stream().filter(p -> p.getNickname().equals(nickname)).findFirst().get().getTilesPicked().size()) {
            showErrorDialog("You can't pick this tile!");
            return;
        }

        if (latestGame.getCurrPlayer().getNickname().equals(nickname) &&
                (latestGame.getModelState() == ModelState.WAITING_1_SELECTION_TILE_FROM_HAND ||
                    latestGame.getModelState() == ModelState.WAITING_2_SELECTION_TILE_FROM_HAND ||
                    latestGame.getModelState() == ModelState.WAITING_3_SELECTION_TILE_FROM_HAND)) {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), tileImage);
            scaleTransition.setToX(tileImage.getScaleX() * 1.1);
            scaleTransition.setToY(tileImage.getScaleY() * 1.1);
            scaleTransition.setCycleCount(2);
            scaleTransition.setAutoReverse(true);

            // Set the notify call to be sent after the animation is finished
            scaleTransition.setOnFinished(event -> {
                System.out.println("Selected tile from hand");
                this.client.eventManager.notify(UserInputEvent.SELECTED_HAND_TILE, tileIndex);
            });

            // Play the animation
            scaleTransition.play();
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    /////////////////////////// VIEW UPDATE METHODS ///////////////////////////
    ///////////////////////////////////////////////////////////////////////////



    public void updateOtherPlayers(GameView gameView) {
        if (nickname == null)
            return;

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (ImmutablePlayer player : gameView.getPlayers()) {
                    // this is only for the other players
                    if (!player.getNickname().equals(nickname)) {
                        // check if a controller is already present for this player
                        if (!otherPlayerItemControllers.containsKey(player.getNickname())) {
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OtherPlayerItem.fxml"));
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
        });
    }



    private void udpateColSelectionArrows() {
        if (latestGame.getCurrPlayer().getNickname().equals(nickname) && latestGame.getModelState() == ModelState.WAITING_SELECTION_BOOKSHELF_COLUMN) {
            colSelectionArrowsGrid.setVisible(true);
            for (int i = 0; i < 5; i++) {
                // show the arrow only if the column has enough space
                ImmutablePlayer me = latestGame.getPlayers().stream().filter(p -> p.getNickname().equals(nickname)).findFirst().get();
                if (me.getBookshelf().getHeight(i) <= Bookshelf.NUMROWS - me.getTilesPicked().size()) {
                    ImageView arrow = (ImageView) colSelectionArrowsGrid.getChildren().get(i);
                    arrow.setImage(new Image("graphics/misc/arrow.png"));
                    arrow.setEffect(new DropShadow(5, Color.ORANGE));
                    arrow.setFitWidth(SEL_COL_ARROW_WIDTH);
                    arrow.setVisible(true);
                    int copyI = i;
                    arrow.setOnMouseClicked(event -> onArrowClicked(copyI, arrow));
                } else {
                    ImageView arrow = (ImageView) colSelectionArrowsGrid.getChildren().get(i);
                    arrow.setImage(null);
                    arrow.setVisible(false);
                }
            }
        } else {
            for (int i = 0; i < 5; i++) {
                ImageView arrow = (ImageView) colSelectionArrowsGrid.getChildren().get(i);
                arrow.setImage(null);
                arrow.setVisible(false);
            }
            colSelectionArrowsGrid.setVisible(false);
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

    private void updateTilesConfirmButton() {
        if (latestGame.getCurrPlayer().getNickname().equals(nickname) && latestGame.getModelState() == ModelState.WAITING_SELECTION_TILE) {
            tilesConfirmButton.setOnMouseClicked(ev -> onConfirmTilesSelection());
            tilesConfirmButton.setVisible(true);
        } else {
            tilesConfirmButton.setOnMouseClicked(null);
            tilesConfirmButton.setVisible(false);
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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                List<CommonGoalCard> commonGoalCards = gameView.getCommonGoals();

                if (commonGoalCards.size() >= 1) {
                    commonGoalCard1.setImage(new Image("graphics/commonGoalCards/common_" + commonGoalCards.get(0).getId() + ".jpg"));
                    commonGoalCard1.setVisible(true);
                    commonGoalCard2.setVisible(false);
                    int k = 0;
                    List<ScoringToken> commonGoalCardTokens = gameView.getCommonGoalTokens(commonGoalCards.get(0).getId());
                    Collections.reverse(commonGoalCardTokens);
                    for (ScoringToken token : commonGoalCardTokens) {
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
                    commonGoalCard2.setImage(new Image("graphics/commonGoalCards/common_" + commonGoalCards.get(1).getId() + ".jpg"));
                    commonGoalCard2.setVisible(true);
                    int k = 0;
                    List<ScoringToken> commonGoalCardTokens = gameView.getCommonGoalTokens(commonGoalCards.get(1).getId());
                    Collections.reverse(commonGoalCardTokens);
                    for (ScoringToken token : commonGoalCardTokens) {
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
        });
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
        Platform.runLater(this::clearTilesPicked);

        for (int i=0; i<tileHand.size(); i++) {
            final int finalI = i;
            Platform.runLater(() -> {
                ImageView tileImage = new ImageView("graphics/tiles/" + tileHand.get(finalI).getItemType() + "_" + tileHand.get(finalI).getItemId() + ".png");
                tileImage.setImage(new Image("graphics/tiles/" + tileHand.get(finalI).getItemType() + "_" + tileHand.get(finalI).getItemId() + ".png"));
                tileImage.setFitHeight(SELECTED_TILE_DIM);
                tileImage.setFitWidth(SELECTED_TILE_DIM);
                tileImage.setEffect(new DropShadow(10, Color.BLACK));
                tileImage.setOnMouseClicked(event -> onTileFromHandClicked(tileImage, finalI));
                tileImage.setVisible(true);

                tilesHandGrid.add(tileImage, finalI, 0);
            });
        }
    }

    private void clearTilesPicked() {
        for (Node node : tilesHandGrid.getChildren()) {
            node.setOnMouseClicked(null);
            Platform.runLater(() -> tilesHandGrid.getChildren().remove(node));
        }
    }


    private void updateMyPersGoal(PersonalGoalCard card) {
        myPersonalGoal.setImage(new Image("graphics/persGoalCards/Personal_Goals" + card.getId() + ".png"));
        myPersonalGoal.setFitHeight(PERSONAL_CARD_HEIGHT);
        myPersonalGoal.setEffect(new DropShadow(10, Color.BLACK));
        myPersonalGoal.setVisible(true);
    }

    /**
     * This method updates the view of the board, by adding or removing tiles from the board's gridPane.
     * @param board the board object that will be shown
     */
    private void updateBoard(ImmutableBoard board) {
        Platform.runLater(this::clearBoard);

        // Fill the board with the tiles
        for (int r = 0; r < Board.DIMBOARD; r++) {
            for (int c = 0; c < Board.DIMBOARD; c++) {
                final int row = r;
                final int col = c;
                Platform.runLater(() -> {
                    if (board.getTile(row, col) != null) {
                        addTileToBoard(board.getTile(row, col), row, col);
                    }
                });
            }
        }
    }


    /**
     * This method allows you to add a tile in the form of an ImageView to the board's gridPane.
     */
    private void addTileToBoard(Tile tile, int row, int col) {
        ImageView tileImage = new ImageView(new Image("graphics/tiles/" + tile.getItemType() + "_" + tile.getItemId() + ".png"));
        tileImage.setVisible(true);
        tileImage.setOnMouseClicked(mouseEvent -> onTileClicked(tileImage, row, col));
        tileImage.setFitHeight(TILE_DIM);
        tileImage.setFitWidth(TILE_DIM);
        tileImage.setEffect(new DropShadow(5, Color.BLACK));

        boardGrid.add(tileImage, col, row);
    }

    /**
     * This method allows you to remove a tile from the board's representation (GridPane).
     */
    private void clearBoard() {
        for (Node node : boardGrid.getChildren()) {
            if (node instanceof ImageView) {
                Platform.runLater(() -> boardGrid.getChildren().remove(node));
            }
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }






}
