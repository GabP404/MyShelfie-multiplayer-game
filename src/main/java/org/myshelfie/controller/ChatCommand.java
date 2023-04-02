package org.myshelfie.controller;

import org.json.JSONObject;

public class ChatCommand implements Command {
    private String message;

    public ChatCommand(String serial) {
        JSONObject jo = new JSONObject(serial);
        message = jo.getString("message");
    }

    @Override
    public void execute() throws InvalidCommand {
        //TODO save message to chat
    }
}
