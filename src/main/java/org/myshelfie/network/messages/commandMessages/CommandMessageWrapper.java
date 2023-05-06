package org.myshelfie.network.messages.commandMessages;

import java.io.Serializable;

/**
 *  this class wraps the message sent by the client or the server. By doing so when a client or server receives
 *  a message it can read the type and then decide what to do with the payload
 */
public class CommandMessageWrapper implements Serializable {

    private UserInputEvent type;
    private CommandMessage message;

    public CommandMessageWrapper(CommandMessage m, UserInputEvent t) {
        type = t;
        message = m;
    }

    public UserInputEvent getType() {
        return type;
    }

    public CommandMessage getMessage() {
        return message;
    }
}
