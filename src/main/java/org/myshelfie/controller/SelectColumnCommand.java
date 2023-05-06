package org.myshelfie.controller;

import org.json.JSONObject;
import org.myshelfie.model.ModelState;
import org.myshelfie.model.Player;
import org.myshelfie.model.WrongArgumentException;

public class SelectColumnCommand implements Command {
    private Player currPlayer;
    private final String nickname;
    private final int selectedColumn;

    private ModelState currentModelState;

    /**
     * @param serial JSON-serialized version of the selectedColumn parameter
     */
    public SelectColumnCommand(Player currPlayer, String serial, ModelState currentModelState) {
        this.currPlayer = currPlayer;
        JSONObject jo = new JSONObject(serial);
        nickname = jo.getString("nickname");
        selectedColumn = jo.getInt("col");
        this.currentModelState = currentModelState;
    }

    public void execute() throws WrongTurnException, InvalidCommand, WrongArgumentException {
        if(!currPlayer.getNickname().equals(nickname)) {
            throw new WrongTurnException();
        }
        if(currentModelState == ModelState.WAITING_SELECTION_BOOKSHELF_COLUMN) throw new InvalidCommand("Waiting for Column Selection ");
        currPlayer.setSelectedColumn(selectedColumn);
    }
}
