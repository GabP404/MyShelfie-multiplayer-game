package org.myshelfie.controller;

import org.json.JSONObject;
import org.myshelfie.model.Player;

public class SelectColumnCommand implements Command {
    private Player player;
    private int selectedColumn;

    /**
     * @param serial JSON-serialized version of the selectedColumn parameter
     */
    public SelectColumnCommand(Player player, String serial) {
        this.player = player;
        JSONObject jo = new JSONObject(serial);
        selectedColumn = jo.getInt("col");
    }

    public void execute() {
        //TODO idk maybe player.setCurrentColumn(selectedColumn) (?)
    }
}
