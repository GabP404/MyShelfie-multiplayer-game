package org.myshelfie.controller;

import org.myshelfie.model.Bookshelf;
import org.myshelfie.model.ModelState;
import org.myshelfie.model.Player;
import org.myshelfie.model.WrongArgumentException;
import org.myshelfie.network.messages.commandMessages.SelectedColumnMessage;

public class SelectColumnCommand implements Command {
    private Player currPlayer;
    private final String nickname;
    private final int selectedColumn;

    private ModelState currentModelState;

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

    public void execute() throws WrongTurnException, InvalidCommand, WrongArgumentException {
        if(!currPlayer.getNickname().equals(nickname)) {
            throw new WrongTurnException();
        }
        if(selectedColumn < 0 || selectedColumn >= Bookshelf.NUMCOLUMNS) throw new WrongArgumentException("Column out of bounds");
        if(currPlayer.getBookshelf().getHeight(selectedColumn) + currPlayer.getTilesPicked().size() > Bookshelf.NUMROWS) throw new WrongArgumentException("Column would be too full");
        if(currentModelState != ModelState.WAITING_SELECTION_BOOKSHELF_COLUMN) throw new InvalidCommand("Waiting for Column Selection ");
        currPlayer.setSelectedColumn(selectedColumn);
    }
}
