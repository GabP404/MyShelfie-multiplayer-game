package org.myshelfie.controller;

import org.json.JSONObject;
import org.myshelfie.model.Player;

public class SelectColumnCommand implements Command {
    private Player player;
    private final String nickname;
    private final int selectedColumn;

    /**
     * @param serial JSON-serialized version of the selectedColumn parameter
     */
    public SelectColumnCommand(Player currPlayer, String serial) {
        this.player = currPlayer;
        JSONObject jo = new JSONObject(serial);
        nickname = jo.getString("nickname");
        selectedColumn = jo.getInt("col");

    }

    public void execute() {
        if(!player.getNickname().equals(nickname))
        {
            //TODO: maybe handle wrong turn command
            return;
        }
        player.setSelectedColumn(selectedColumn);
    }
}
