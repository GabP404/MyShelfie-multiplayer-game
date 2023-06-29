package org.myshelfie.network.messages.commandMessages;

import java.io.Serializable;

/**
 * This message is sent by the {@link org.myshelfie.network.client.Client} to the server when
 * the client wants to join a game or rejoin after disconnection.
 */
public class NicknameMessage extends CommandMessage implements Serializable {
    public NicknameMessage(String nickname) {
        super(nickname);
    }
}
