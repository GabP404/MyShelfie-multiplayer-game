package org.myshelfie.network;

import org.json.JSONObject;

/**
 * This class represents the command used to send the choice of the column of the bookshelf in which the user
 * wants to put their tile(s)
 */
public class SelectedColumnMessage extends CommandMessage {
    int selectedColumn;

    /**
     * @param nickname   Nickname of the player sending the message
     * @param col        column selected by the player
     */
    public SelectedColumnMessage(String nickname, int col) {
        super(nickname);
        selectedColumn = col;
    }

    @Override
    public String toString() {
        JSONObject jo = new JSONObject();
        jo.put("nickname", nickname);
        jo.put("col", selectedColumn);
        return jo.toString();
    }
}
