package org.myshelfie.network;

import org.json.JSONObject;

/**
 * This class represents the command sent to the server to send a message to the chat
 */
public class ChatCommandMessage extends CommandMessage {
    private final String message;
    private final String whisper;

    /**
     * @param nickname  Nickname of the player sending the message
     * @param s         chat message
     * @param whisp      recipient of the message (if it's a whisper)
     */
    public ChatCommandMessage(String nickname, String s, String whisp) {
        super(nickname);
        message = s;
        whisper = whisp;
    }

    @Override
    public String toString() {
        JSONObject jo = new JSONObject();
        jo.put("nickname", nickname);
        jo.put("message", message);
        jo.put("whisper", whisper);
        return jo.toString();
    }
}
