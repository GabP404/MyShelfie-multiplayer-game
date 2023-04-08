package org.myshelfie.network.messages.commandMessages;

import org.json.JSONObject;

/**
 * This class represents the command used to send the choice of the column of the bookshelf in which the user
 * wants to put their tile(s)
 */
public class SelectedColumnMessage extends CommandMessage {
    int selectedColumn;

    public SelectedColumnMessage(int col) {
        selectedColumn = col;
    }

    @Override
    public String toString() {
        JSONObject jo = new JSONObject();
        jo.put("col", selectedColumn);
        return jo.toString();
    }
}
