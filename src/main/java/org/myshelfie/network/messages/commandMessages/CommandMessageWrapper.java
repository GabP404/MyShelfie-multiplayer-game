package org.myshelfie.network.messages.commandMessages;

import org.myshelfie.network.client.UserInputEvent;

import java.io.Serializable;

/**
 *  This class wraps the message sent by the client or the server. By doing so when a client or server receives
 *  a message it can read the type and then decide what to do with the payload
 */
public class CommandMessageWrapper implements Serializable {

    private final UserInputEvent type;
    private final CommandMessage message;

    public CommandMessageWrapper(CommandMessage m, UserInputEvent t) {
        type = t;
        message = m;
    }

    /**
     * @return The type of event that has triggered the sending of this message
     */
    public UserInputEvent getType() {
        return type;
    }

    /**
     * @return The actual message wrapped by this wrapper
     */
    public CommandMessage getMessage() {
        return message;
    }
}
