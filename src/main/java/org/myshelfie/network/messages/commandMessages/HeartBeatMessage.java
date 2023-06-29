package org.myshelfie.network.messages.commandMessages;

import java.io.Serializable;

/**
 * This is a speccial kind of {@link CommandMessage} since it is not linked to any
 * Command class. This message serves the purpose of continuous ping between Client and Server.
 */
public class HeartBeatMessage extends CommandMessage implements Serializable {
    public HeartBeatMessage(String nickname) {
        super(nickname);
    }
}
