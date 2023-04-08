package org.myshelfie.network.messages.commandMessages;

import org.json.JSONObject;

/**
 * This class represents the command sent to the server to send a message to the chat
 */
public class ChatCommandMessage extends CommandMessage {
    private final String message;

    public ChatCommandMessage(String s) {
        message = s;
    }

    @Override
    public String toString() {
        JSONObject jo = new JSONObject();
        jo.put("message", message);
        return jo.toString();
    }
}
