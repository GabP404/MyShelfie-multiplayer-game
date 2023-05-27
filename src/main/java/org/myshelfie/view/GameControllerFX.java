package org.myshelfie.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import org.myshelfie.model.Tile;

import java.net.URL;
import java.util.ResourceBundle;

public class GameControllerFX implements Initializable {
    @FXML
    private ImageView my_bookshelf;

    @FXML
    private ImageView boardImage;

    @FXML
    private GridPane boardGrid;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        boardGrid.prefWidthProperty().bind(boardImage.fitWidthProperty());
        boardGrid.prefHeightProperty().bind(boardImage.fitHeightProperty());
    }

    /**
     * This method allows you to add a tile in the form of an ImageView to the board's gridPane.
     * TODO: add checks to make sure the add is valid
     */
    public void addTileToBoard(Tile tile, int row, int col) {
        ImageView tileImage = (ImageView) boardGrid.getChildren().get(col + row * boardGrid.getColumnCount());
        tileImage.setImage(new Image("graphics/tiles/" + tile.getItemType() + "_" + tile.getItemId() + ".png"));
        tileImage.fitWidthProperty().bind(boardGrid.widthProperty().divide(boardGrid.getColumnConstraints().size()));
        tileImage.fitHeightProperty().bind(boardGrid.heightProperty().divide(boardGrid.getRowConstraints().size()));
        // scale down to allow a little space between tiles
        tileImage.setScaleX(0.9);
        tileImage.setScaleY(0.9);
        tileImage.setVisible(true);
    }
}
