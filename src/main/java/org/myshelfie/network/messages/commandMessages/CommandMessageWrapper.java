package org.myshelfie.network.messages.commandMessages;

/**
 *  this class wraps the message sent by the client or the server. By doing so when a client or server receives
 *  a message it can read the type and then decide what to do with the payload
 */

public class CommandMessageWrapper {

    private UserInputEventType type;
    private String message;

    public CommandMessageWrapper(CommandMessage m, UserInputEventType t) {
        type = t;
        message = ""; //TODO JSON ser
    }

    public UserInputEventType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
