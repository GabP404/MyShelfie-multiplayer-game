package org.myshelfie.network.messages.commandMessages;

import java.io.Serializable;

/**
 * This message is sent by the client to the server when the client wants to join (or rejoin after disconnection) a game.
 */
public class NicknameMessage extends CommandMessage implements Serializable {
    public NicknameMessage(String nickname) {
        super(nickname);
    }
}
