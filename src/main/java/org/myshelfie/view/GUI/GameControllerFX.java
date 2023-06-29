package org.myshelfie.view.GUI;

import javafx.geometry.Pos;
import javafx.scene.shape.Rectangle;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import org.myshelfie.controller.Configuration;
import org.myshelfie.model.*;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.client.UserInputEvent;
import org.myshelfie.network.messages.gameMessages.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Controller for the GameFXML scene, i.e. the main game scene.
 * This class is responsible for handling all the events that happen in the game, updating
 * the GUI accordingly to the events received by the server, and collecting the input from the user
 * and notify the {@link org.myshelfie.network.EventManager EventManager} to update the Server.
 */
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
    private StackPane overlay;
    @FXML
    private Rectangle overlayBackground;
    @FXML
    private ImageView spinner;
    @FXML
    private Button tilesConfirmButton;
    @FXML
    private GridPane tilesHandGrid;
    @FXML
    private VBox updatesVBox;
    @FXML
    private Label gameNameLabel;
    @FXML
    private ImageView bookshelfPointsTable;
    @FXML
    private StackPane globalPane;

    private String easterEgg = "";
    private boolean firstSetupDone = false;
    private String nickname = null;
    private Map<String, OtherPlayerItemController> otherPlayerItemControllers;
    GameView latestGame;
    List<LocatedTile> unconfirmedSelectedTiles;
    private Client client;
    private boolean isPaused = false;

    final int TOKEN_DIM = 50;
    final int PERSONAL_CARD_HEIGHT = 200;
    final int SELECTED_TILE_DIM = 55;
    final int SEL_COL_ARROW_WIDTH = 50;
    final double TILE_DIM = 45;


    /**
     * Initializes the controller. This is called automatically by JavaFX.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        latestGame = null;
        otherPlayerItemControllers = new HashMap<>();
        unconfirmedSelectedTiles = new ArrayList<>();

        overlayBackground.widthProperty().bind(overlay.widthProperty());
        overlayBackground.heightProperty().bind(overlay.heightProperty());

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

        bookshelfPointsTable.setVisible(true);
        setOnHoverZoom(bookshelfPointsTable, 1, 1.4);
    }

    /**
     * Set the nickname of the player that is using this GUI.
     * @param nickname The nickname of the player
     */
    public void setMyNickname(String nickname) {
        this.nickname = nickname;
        myNickname.setText(nickname);
        myNickname.setStyle("-fx-font-weight: bold");
        myNickname.setVisible(true);
    }

    /**
     * Link this controller to the client that is using it.
     * @param client The client that is using this controller
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /////////////////////////////////////////////////////////////////////////////
    ///////////////////////////// MAIN UPDATE METHOD ////////////////////////////
    /////////////////////////////////////////////////////////////////////////////

    /**
     * Forwards the update of the GUI by scheduling it using a Platform.runLater() call
     * is called directly by {@link ViewGUI#update(GameView, GameEvent)}.
     * @param ev The event that triggered the update
     * @param game The game view object containing the updated game state
     */
    public void update(GameEvent ev, GameView game) {
        Platform.runLater(() -> updateButForReal(ev, game));
    }

    /**
     * This is the actual update method. Based on the type of {@link GameEvent} this method
     * chooses which items of the view will be updated and calls the corresponding methods.
     * @param ev The {@link GameEvent} that triggered the update
     * @param game The {@link GameView} object containing the updated game state
     */
    private void updateButForReal(GameEvent ev, GameView game) {
        latestGame = game;
        System.out.println("STATUS: "+ game.getModelState());
        ImmutablePlayer me = game.getPlayers().stream().filter(p -> p.getNickname().equals(nickname)).findFirst().get();

        if (game.getModelState().equals(ModelState.PAUSE)) {
            updateOtherPlayers(game);
            updateHelper();
            isPaused = true;
            showInfoDialog("The game is paused because you are the only online player!");
            return;
        }

        if (isPaused) {
            if (!game.getModelState().equals(ModelState.PAUSE)) {
                isPaused = false;
                showInfoDialog("The game resumed!");
            }
        }

        if (ev != GameEvent.PLAYER_ONLINE_UPDATE) {
            unconfirmedSelectedTiles.clear();
        }

        if (!firstSetupDone) {
            overlay.setVisible(false);
            updateEverything(game);
            updateGameName(game.getGameName());
            firstSetupDone = true;
            return;
        }

        switch (ev) {
            case BOARD_UPDATE -> {
                // Update board
                updateBoard(game.getBoard());
                updateMyBookshelf(me.getBookshelf());
                udpateColSelectionArrows();
                updateCommonGoalCards(game);
                updateMyCommonGoalToken(me.getCommonGoalTokens());
            }
            case TILES_PICKED_UPDATE -> {
                updateBoard(game.getBoard());
            }
            case SELECTED_COLUMN_UPDATE -> {
                udpateColSelectionArrows();
            }
            case TOKEN_STACK_UPDATE, CURR_PLAYER_UPDATE -> {
                updateCommonGoalCards(game);
                updateMyCommonGoalToken(me.getCommonGoalTokens());
            }
            case TOKEN_UPDATE -> {
                updateMyPersGoal(me.getPersonalGoal());
                updateOtherPlayers(game);
                updateCommonGoalCards(game);
                updateMyCommonGoalToken(me.getCommonGoalTokens());
            }
            case FINAL_TOKEN_UPDATE -> {
                updateMyFinalToken(game.getPlayers().stream().filter(p -> p.getNickname().equals(nickname)).findFirst().get().getHasFinalToken());
                updateCommonGoalCards(game);
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
        updateAmICurrPlayer(game.getCurrPlayer().getNickname().equals(nickname));
        updateHelper();
        updateTilesConfirmButton();
        updateMyBookshelf(me.getBookshelf());
        updateMyPersGoal(me.getPersonalGoal());
        updateMyTilesPicked(me.getTilesPicked());
        // Update other players (note that they are controlled by a different controller)
        updateOtherPlayers(game);
    }

    /**
     * Updates all the possible items of the view, regardless of the type of event that triggered the update.
     * @param game The {@link GameView} object containing the updated game state.
     */
    private void updateEverything(GameView game) {
        // Update helper
        updateHelper();
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
                        //check if the tiles can fit in the bookshelf
                        if (latestGame.getCurrPlayer().getBookshelf().getMinHeight()+ unconfirmedSelectedTiles.size() > Bookshelf.NUMROWS)
                        {
                            unconfirmedSelectedTiles.remove(t);
                            showErrorDialog("You can't pick this tile because it doesn't fit in your bookshelf!");
                            return;
                        }
                        tileImage.setEffect(new DropShadow(15, Color.GREEN));
                        tileImage.toFront();

                        // Create a ScaleTransition with desired properties
                        double finalScale = 1.2; // The final scale value
                        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), tileImage);
                        scaleTransition.setToX(finalScale);
                        scaleTransition.setToY(finalScale);
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
                    unconfirmedSelectedTiles.remove(t);
                    if(!isTilesGroupSelectable(latestGame.getBoard(), unconfirmedSelectedTiles))
                    {
                        unconfirmedSelectedTiles.add(t);
                        showErrorDialog("You can't un-pick this tile!");
                        return;
                    }
                    tileImage.setScaleX(1);
                    tileImage.setScaleY(1);
                    tileImage.setEffect(new DropShadow(5, Color.BLACK));
                    System.out.println("Deselected tile: " + row + " " + col);
                }
            }
        } else {
            showErrorDialog("It's not your turn!");
        }
    }

    /**
     * Method that is called every time the user tries to select a Tile.
     * It is used to check wheter the current list of tiles is valid or not.
     * @param board The board of the game
     * @param unconfirmedSelectedTiles The temporary list of tiles that the user is trying to select
     * @return True if the list is valid, false otherwise
     */
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


    /**
     * Method that is called when a bookshelf column is selected. It's bound to the on click event of the arrowImage object.
     * Calls the {@link org.myshelfie.network.EventManager#notify notify} method to send this information to the server.
     * @param column The index of the bookshelf's column that has been selected
     * @param arrowImage The ImageView object representing the arrow that has been clicked
     */
    private void onArrowClicked(int column, ImageView arrowImage) {
        //control that prevents the player from selecting a column that cannot contain the tiles he selected
        if(latestGame.getCurrPlayer().getBookshelf().getHeight(column) + latestGame.getCurrPlayer().getTilesPicked().size() > Bookshelf.NUMROWS)
        {
            showErrorDialog("You can't fit the selected tiles in this column!");
            return;
        }
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

    /**
     * Method called when the user clicks on the confirm button.
     * It's bound to the on click event of the confirmButton object.
     * It sends the selected tiles to the server by calling the
     * {@link org.myshelfie.network.EventManager#notify notify} method.
     */
    private void onConfirmTilesSelection() {
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

    /**
     * Method called when the user clicks on a tile from their hand.
     * It's bound to the on click event of the tileImage object.
     * It sends the selected tile index to the server by calling the
     * {@link org.myshelfie.network.EventManager#notify notify} method.
     * @param tileImage The ImageView object representing the tile that has been clicked
     * @param tileIndex The index of the tile that has been clicked
     */
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
                tileImage.setOnMouseClicked(null);
                tileImage.setVisible(false);
            });

            // Play the animation
            scaleTransition.play();
        }
    }

    ////////////////////////////////////////////////////////////////////
    ////////////////////// DIALOG MESSAGE METHODS //////////////////////
    ////////////////////////////////////////////////////////////////////

    /**
     * Update method used to show a dialog with an error message.
     * @param message The message to show
     */
    public void showErrorDialog(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("A small message for you from the MyShelfie overlord");
            alert.setContentText(message);

            alert.showAndWait();
        });
    }

    /**
     * Update method used to show a dialog with an info message.
     * @param message The message to show
     */
    public void showInfoDialog(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("FYI");
            alert.setHeaderText("A small message for you by the MyShelfie overlord");
            alert.setContentText(message);

            alert.showAndWait();
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    /////////////////////////// VIEW UPDATE METHODS ///////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    /**
     * This method makes use of the {@link OtherPlayerItemController} class to update the
     * view of the other players items.
     * @param gameView The {@link GameView} object containing the updated game state
     */
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

    private void updateGameName(String gameName) {
        Platform.runLater(() -> {
            gameNameLabel.setText(gameName);
            gameNameLabel.setFont(Font.font("System", FontWeight.BOLD, 30));
            gameNameLabel.setVisible(true);
        });
    }

    /**
     * This method updates the view of the arrows used to select the column.
     * They're shown only if the current state is {@link ModelState#WAITING_SELECTION_BOOKSHELF_COLUMN}.
     */
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
                    setOnHoverZoom(arrow, 1, 1.075);
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

    /**
     * This method updates the view of the nickname for the player using this view.
     * If this player is the current one, a flag and a shadow are added to the nickname.
     * @param amICurrPlayer True if the player using this view is the current player
     */
    private void updateAmICurrPlayer(boolean amICurrPlayer) {
        Platform.runLater(() -> {
            if (amICurrPlayer) {
                myNickname.setFont(Font.font("System", FontWeight.BOLD, 20));
                myNickname.setEffect(new DropShadow(15, Color.WHITE));
                myNickname.setText(nickname + " \uD83C\uDFF3\uFE0F");
                myNickname.setVisible(true);
            } else {
                myNickname.setFont(Font.font("System", FontWeight.BOLD, 12));
                myNickname.setEffect(null);
                myNickname.setText(nickname);
                myNickname.setVisible(true);
            }
        });
    }

    /**
     * This method is used to enable and show the confirm button for the tiles' selection,
     * when the game state is {@link ModelState#WAITING_SELECTION_TILE}.
     */
    private void updateTilesConfirmButton() {
        if (latestGame.getCurrPlayer().getNickname().equals(nickname) && latestGame.getModelState() == ModelState.WAITING_SELECTION_TILE) {
            tilesConfirmButton.setOnMouseClicked(ev -> onConfirmTilesSelection());
            tilesConfirmButton.setVisible(true);
        } else {
            tilesConfirmButton.setOnMouseClicked(null);
            tilesConfirmButton.setVisible(false);
        }
    }

    /**
     * This method updates the view of the bookshelf of the player using this view.
     * @param bookshelf The {@link ImmutableBookshelf} object containing the updated bookshelf
     */
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
     * @param gameView The {@link GameView} object containing the updated information
     */
    private void updateCommonGoalCards(GameView gameView) {
        List<CommonGoalCard> commonGoalCards = gameView.getCommonGoals();

        if (commonGoalCards.size() >= 1) {
            String cardId = commonGoalCards.get(0).getId();
            commonGoalCard1.setImage(new Image("graphics/commonGoalCards/common_" + cardId + ".jpg"));
            Tooltip.install(commonGoalCard1, new Tooltip(Configuration.getCommonGoalCardDescription(cardId)));
            commonGoalCard1.setVisible(true);
            commonGoalCard2.setVisible(false);
            int k = 0;
            List<ScoringToken> commonGoalCardTokens = gameView.getCommonGoalTokens(cardId);
            Collections.reverse(commonGoalCardTokens);

            // remove all the tokens from the pane
            AnchorPane pane = (AnchorPane) commonGoalCard1.getParent();
            pane.getChildren().removeIf(node -> node instanceof ImageView && node != commonGoalCard1);
            for (ScoringToken token : commonGoalCardTokens) {
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
            String cardId = commonGoalCards.get(1).getId();
            commonGoalCard2.setImage(new Image("graphics/commonGoalCards/common_" + cardId + ".jpg"));
            Tooltip.install(commonGoalCard2, new Tooltip(Configuration.getCommonGoalCardDescription(cardId)));
            commonGoalCard2.setVisible(true);
            int k = 0;
            List<ScoringToken> commonGoalCardTokens = gameView.getCommonGoalTokens(cardId);
            Collections.reverse(commonGoalCardTokens);

            // remove all the tokens from the pane
            AnchorPane pane = (AnchorPane) commonGoalCard2.getParent();
            pane.getChildren().removeIf(node -> node instanceof ImageView && node != commonGoalCard2);
            for (ScoringToken token : commonGoalCardTokens) {
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


    /**
     * This method updates the view of the common goal tokens obtained by the player using this view.
     * @param myCommonGoalTokens The list of {@link ScoringToken} objects representing the obatined tokens
     */
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

    /**
     * Method used to show the final token of the player using this view if he/she has it.
     * @param hasFinalToken True if the player has the final token, false otherwise
     */
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

    /**
     * Updates the view of the tiles in the hand of the player using this view.
     * @param tileHand The list of {@link Tile} objects representing the tiles in the hand
     */
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
                setOnHoverZoom(tileImage, 1, 1.05);
                tileImage.setVisible(true);

                tilesHandGrid.add(tileImage, finalI, 0);
            });
        }
    }

    /**
     * Clears the list of tiles in the hand of the player using this view.
     */
    private void clearTilesPicked() {
        for (Node node : tilesHandGrid.getChildren()) {
            node.setOnMouseClicked(null);
            Platform.runLater(() -> tilesHandGrid.getChildren().remove(node));
        }
    }

    /**
     * Updates the view of the helper panel showing the current state of the game.
     */
    private void updateHelper() {
        // Clear helper area
        for (Node node : updatesVBox.getChildren())
            Platform.runLater(() -> {
                node.setVisible(false);
                updatesVBox.getChildren().remove(node);
            });

        if (latestGame.getModelState() == ModelState.PAUSE) {
            // Signals my turn!
            Platform.runLater(() -> {
                Label helperPause = new Label("The game is paused because you're\n the only player online.");
                helperPause.setFont(Font.font("System", FontWeight.BOLD, 18));
                helperPause.setEffect(new DropShadow(10, Color.WHITE));
                helperPause.setAlignment(Pos.CENTER);
                updatesVBox.getChildren().add(helperPause);
            });
            return;
        }

        // Add helper text
        if (latestGame.getCurrPlayer().getNickname().equals(nickname)) {
            // Signals my turn!
            Platform.runLater(() -> {
                Label turnLabel = new Label("It's your turn!");
                turnLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
                turnLabel.setEffect(new DropShadow(10, Color.WHITE));
                turnLabel.setAlignment(Pos.CENTER);
                updatesVBox.getChildren().add(turnLabel);
            });

            // Add helper text
            String updateString = "";
            switch (latestGame.getModelState()) {
                case WAITING_SELECTION_TILE -> updateString = updateString + "Select the tiles from the board. \nClick Confirm when you are done.";
                case WAITING_SELECTION_BOOKSHELF_COLUMN -> updateString = updateString + "Choose the column in which \nyou want to insert the tiles.";
                case WAITING_1_SELECTION_TILE_FROM_HAND, WAITING_2_SELECTION_TILE_FROM_HAND, WAITING_3_SELECTION_TILE_FROM_HAND -> updateString = updateString + "Select one by one the tiles\n to insert from your hand.";
                case END_GAME -> updateString = "The game is ended.";
            }
            String finalUpdateString = updateString;
            Platform.runLater(() -> {
                Label updatesLabel = new Label(finalUpdateString);
                updatesLabel.setText(finalUpdateString);
                updatesLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
                updatesLabel.setAlignment(Pos.CENTER);
                updatesLabel.setEffect(new DropShadow(10, Color.WHITE));
                updatesLabel.setVisible(true);
                updatesVBox.getChildren().add(updatesLabel);
            });
        } else {
            // Other player's turn
            String helperString = "";
            helperString = latestGame.getCurrPlayer().getNickname();
            switch (latestGame.getModelState()) {
                case WAITING_SELECTION_TILE -> helperString = helperString + " is selecting\ntiles from the board.\n ";
                case WAITING_SELECTION_BOOKSHELF_COLUMN -> helperString = helperString + "\nis choosing the column.\n ";
                case WAITING_1_SELECTION_TILE_FROM_HAND, WAITING_2_SELECTION_TILE_FROM_HAND, WAITING_3_SELECTION_TILE_FROM_HAND -> helperString = helperString + " is inserting\nthe tiles in the bookshelf.\n ";
                case END_GAME -> helperString = "The game is ended.";
            }
            String finalHelperString = helperString;
            Platform.runLater(() -> {
                Label updatesLabel = new Label(finalHelperString);
                updatesLabel.setText(finalHelperString);
                updatesLabel.setAlignment(Pos.CENTER);
                updatesLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
                updatesLabel.setEffect(new DropShadow(10, Color.WHITE));
                updatesLabel.setVisible(true);
                updatesVBox.getChildren().add(updatesLabel);
            });
        }
    }

    /**
     * Updates the view of the personal goals of the player using this view.
     * @param card The personal goal card that will be shown
     */
    private void updateMyPersGoal(PersonalGoalCard card) {
        myPersonalGoal.setImage(new Image("graphics/persGoalCards/Personal_Goals" + card.getId() + ".png"));
        myPersonalGoal.setFitHeight(PERSONAL_CARD_HEIGHT);
        myPersonalGoal.setEffect(new DropShadow(10, Color.BLACK));

        setOnHoverZoom(myPersonalGoal, 1, 1.3);

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
     * @param tile The tile that will be added
     * @param row The row in which the tile will be added
     * @param col The column in which the tile will be added
     */
    private void addTileToBoard(Tile tile, int row, int col) {
        String extra = "";
        if (tile.getItemType() == ItemType.TROPHY)
            extra = easterEgg;
        ImageView tileImage = new ImageView(new Image("graphics/tiles/" + tile.getItemType() + "_" + tile.getItemId() + extra + ".png"));
        tileImage.setVisible(true);
        tileImage.setOnMouseClicked(mouseEvent -> onTileClicked(tileImage, row, col));
        tileImage.setFitHeight(TILE_DIM);
        tileImage.setFitWidth(TILE_DIM);
        tileImage.setEffect(new DropShadow(5, Color.BLACK));

        // Set on hover effect
        setOnHoverZoom(tileImage, 1, 1.07);

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

    ////////////////////////////////////////////////////////////////////////////
    /////////////////////////// UTILITY METHODS ///////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Utility method used to create an onHover effect for a generic {@link Node}.
     * @param item The node on which the effect will be applied
     * @param defaultScale The default scale of the node
     * @param zoomedScale The scale of the node when the mouse is over it
     */
    private void setOnHoverZoom(Node item, double defaultScale, double zoomedScale) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), item);
        scaleTransition.setToX(zoomedScale);
        scaleTransition.setToY(zoomedScale);
        // Create a ScaleTransition for revert to initial size
        ScaleTransition scaleRevertTransition = new ScaleTransition(Duration.millis(200), item);
        scaleRevertTransition.setToX(defaultScale);
        scaleRevertTransition.setToY(defaultScale);
        // Add event handlers to the card
        item.setOnMouseEntered(event -> scaleTransition.playFromStart());
        item.setOnMouseExited(event -> scaleRevertTransition.playFromStart());
    }

    public void setEasterEgg(String str) {
        easterEgg = str;
        applyEasterEgg();
    }

    private void applyEasterEgg() {
        boardImage.setImage(new Image("/graphics/boards/livingroom" + easterEgg + ".png"));
        myBookshelfImage.setImage(new Image("/graphics/boards/bookshelf" + easterEgg + ".png"));
        globalPane.setStyle("-fx-background-image: url('/graphics/misc/parquet" + easterEgg + ".jpg')");
    }
}
