package org.myshelfie.network.messages.commandMessages;

import java.io.Serializable;

/**
 * This class represents the command used to send the choice of the column of the bookshelf in which the user
 * wants to put their tile(s)
 */
public class SelectedColumnMessage extends CommandMessage implements Serializable {
    int selectedColumn;

    /**
     * @param nickname   Nickname of the player sending the message
     * @param col        column selected by the player
     */
    public SelectedColumnMessage(String nickname, int col) {
        super(nickname);
        selectedColumn = col;
    }

    public int getSelectedColumn() {
        return selectedColumn;
    }
}
