package org.myshelfie.network.messages.commandMessages;

import org.json.JSONObject;

import java.io.Serializable;

/**
 *  this class wraps the message sent by the client or the server. By doing so when a client or server receives
 *  a message it can read the type and then decide what to do with the payload
 */
//TODO switch to serialization
public class CommandMessageWrapper implements Serializable {

    private UserInputEvent type;
    private String message;

    public CommandMessageWrapper(CommandMessage m, UserInputEvent t) {
        type = t;
        message = m.toString();
    }

    public CommandMessageWrapper(String serialized) {
        JSONObject obj = new JSONObject(serialized);
        this.type = UserInputEvent.valueOf(obj.getString("type"));
        this.message = obj.getString("message");
    }

    @Override
    public String toString() {
        JSONObject obj = new JSONObject();
        obj.put("type", type.toString());
        obj.put("message", message);
        return obj.toString();
    }

    public UserInputEvent getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
