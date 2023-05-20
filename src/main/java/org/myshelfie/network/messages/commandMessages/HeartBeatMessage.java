package org.myshelfie.network.messages.commandMessages;

import java.io.Serializable;

public class HeartBeatMessage extends CommandMessage implements Serializable {
    public HeartBeatMessage(String nickname) {
        super(nickname);
    }
}
