package org.myshelfie.view.GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.myshelfie.network.messages.gameMessages.GameView;
import org.myshelfie.network.messages.gameMessages.ImmutablePlayer;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * This class is the controller for the view of the end game screen.
 */
public class EndGameControllerFX implements Initializable {

    @FXML
    private Button ExitGame_BTN;


    @FXML
    private VBox leaderboardPane;

    @FXML
    private Label winner_LBL;

    @FXML
    private TableView<ImmutablePlayer> leaderboardTable;
    private ObservableList<ImmutablePlayer> leaderboardData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void createRankingTable(GameView gameView) {
        leaderboardTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ImmutablePlayer, String> nicknameColumn = new TableColumn<>("Nickname");
        nicknameColumn.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        nicknameColumn.getStyleClass().add("table-column");

        TableColumn<ImmutablePlayer, Integer> commonGoalPointsColumn = new TableColumn<>("Common Goal");
        commonGoalPointsColumn.setCellValueFactory(new PropertyValueFactory<>("commonGoalPoints"));
        commonGoalPointsColumn.getStyleClass().add("table-column");


        TableColumn<ImmutablePlayer, Integer> personalGoalPointsColumn = new TableColumn<>("Personal Goal");
        personalGoalPointsColumn.setCellValueFactory(new PropertyValueFactory<>("personalGoalPoints"));
        personalGoalPointsColumn.getStyleClass().add("table-column");

        TableColumn<ImmutablePlayer, Integer> bookshelfPointsColumn = new TableColumn<>("Bookshelf");
        bookshelfPointsColumn.setCellValueFactory(new PropertyValueFactory<>("bookshelfPoints"));
        bookshelfPointsColumn.getStyleClass().add("table-column");


        TableColumn<ImmutablePlayer, Boolean> endTokenColumn = new TableColumn<>("End Token");
        endTokenColumn.setCellValueFactory(new PropertyValueFactory<>("hasFinalToken"));
        endTokenColumn.getStyleClass().add("table-column");
        endTokenColumn.setCellFactory(
                column -> new TableCell<ImmutablePlayer, Boolean>() {
                    private final ImageView imageView = new ImageView();

                    {
                        imageView.setFitWidth(30);
                        imageView.setFitHeight(30);
                    }

                    @Override
                    protected void updateItem(Boolean hasFinalToken, boolean empty) {
                        super.updateItem(hasFinalToken, empty);
                        if (hasFinalToken == null || empty) {
                            setGraphic(null);
                        } else {
                            if (hasFinalToken) {
                                Image image = new Image("/graphics/tokens/endGame.jpg");
                                imageView.setImage(image);
                                setGraphic(imageView);
                            } else {
                                setGraphic(null);
                            }
                        }
                    }
                }
        );


        TableColumn<ImmutablePlayer, Integer> totalPointsColumn = new TableColumn<>("Total Points");
        totalPointsColumn.setCellValueFactory(new PropertyValueFactory<>("totalPoints"));
        totalPointsColumn.getStyleClass().add("table-column");

        /*
        TableColumn<ImmutablePlayer, Boolean> winnerColumn = new TableColumn<>("Winner");
        winnerColumn.setCellValueFactory(new PropertyValueFactory<>("winner"));
        winnerColumn.getStyleClass().add("table-column");
        winnerColumn.setCellFactory(column -> new TableCell<ImmutablePlayer, Boolean>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(30);
                imageView.setFitHeight(30);
            }

            @Override
            protected void updateItem(Boolean winner, boolean empty) {
                super.updateItem(winner, empty);
                if (winner == null || empty) {
                    setGraphic(null);
                } else {
                    if (winner) {
                        System.out.println("Winner");
                        Image image = new Image("graphics/tiles/TROPHY_3.png");
                        imageView.setImage(image);
                        setGraphic(imageView);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

         */

        nicknameColumn.setCellFactory(column -> (TableCell<ImmutablePlayer, String>) createOfflineCellFactory());
        commonGoalPointsColumn.setCellFactory(column -> (TableCell<ImmutablePlayer, Integer>) createOfflineCellFactory());
        personalGoalPointsColumn.setCellFactory(column -> (TableCell<ImmutablePlayer, Integer>) createOfflineCellFactory());
        bookshelfPointsColumn.setCellFactory(column -> (TableCell<ImmutablePlayer, Integer>) createOfflineCellFactory());
        totalPointsColumn.setCellFactory(column -> (TableCell<ImmutablePlayer, Integer>) createOfflineCellFactory());



        leaderboardTable.getColumns().addAll(nicknameColumn, commonGoalPointsColumn,
                personalGoalPointsColumn, bookshelfPointsColumn, endTokenColumn,
                totalPointsColumn);

        leaderboardData = FXCollections.observableArrayList();
        leaderboardTable.setItems(leaderboardData);
        leaderboardData.clear();

        for (ImmutablePlayer immutablePlayer : gameView.getPlayers()) {
            leaderboardData.add(immutablePlayer);
        }

        List<ImmutablePlayer> winners =  gameView.getPlayers().stream().filter(ImmutablePlayer::isWinner).toList();
        if (winners.size() > 1) {
            String nicknamesWinners = winners.get(0).getNickname();
            for (int i = 1; i < winners.size(); i++) {
                nicknamesWinners += " & " + winners.get(i).getNickname();
            }
            winner_LBL.setText("The winners are: " + nicknamesWinners);
        }else if(winners.size() == 1)
            winner_LBL.setText("The winner is: " + winners.get(0).getNickname());
        else
            winner_LBL.setText("No winner");
    }

    private TableCell<ImmutablePlayer, ?> createOfflineCellFactory() {
        return new TableCell<ImmutablePlayer, Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    ImmutablePlayer player = getTableView().getItems().get(getIndex());
                    if (!player.isOnline()) {
                        setStyle("-fx-text-fill: gray;");
                    } else {
                        setStyle(""); // Reset the style
                    }
                }
            }
        };
    }

    public void handleExitGame() {
        javafx.application.Platform.exit();
        System.exit(0);
    }
}
