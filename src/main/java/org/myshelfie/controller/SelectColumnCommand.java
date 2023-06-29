package org.myshelfie.controller;

import org.myshelfie.model.Bookshelf;
import org.myshelfie.model.ModelState;
import org.myshelfie.model.Player;
import org.myshelfie.model.WrongArgumentException;
import org.myshelfie.network.messages.commandMessages.SelectedColumnMessage;

/**
 * This class implements a command (following the command design pattern) that selects a column in the bookshelf.
 */
public class SelectColumnCommand implements Command {
    private final Player currPlayer;
    private final String nickname;
    private final int selectedColumn;
    private final ModelState currentModelState;

    /**
     * @param command  The command message sent by the client
     * @param currPlayer The player who sent the command
     * @param currentModelState The current model state
     */
    public SelectColumnCommand(Player currPlayer, SelectedColumnMessage command, ModelState currentModelState) {
        this.currPlayer = currPlayer;
        nickname = command.getNickname();
        selectedColumn = command.getSelectedColumn();
        this.currentModelState = currentModelState;
    }

    /**
     * Set the player's selected column after checking if the command is valid.
     * @throws WrongTurnException If it's not the player's turn
     * @throws InvalidCommandException If the command is not valid in the current model state
     * @throws WrongArgumentException If the column is out of bounds or if there are too many tiles in the hand to fit that column
     */
    public void execute() throws WrongTurnException, InvalidCommandException, WrongArgumentException {
        if(!currPlayer.getNickname().equals(nickname)) {
            throw new WrongTurnException();
        }
        if(selectedColumn < 0 || selectedColumn >= Bookshelf.NUMCOLUMNS) throw new WrongArgumentException("Column out of bounds");
        if(currPlayer.getBookshelf().getHeight(selectedColumn) + currPlayer.getTilesPicked().size() > Bookshelf.NUMROWS) throw new WrongArgumentException("Too many tiles for this column");
        if(currentModelState != ModelState.WAITING_SELECTION_BOOKSHELF_COLUMN) throw new InvalidCommandException("Waiting for Column Selection ");
        currPlayer.setSelectedColumn(selectedColumn);
    }
}
