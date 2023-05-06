package org.myshelfie.network.messages.commandMessages;

import java.util.UUID;

public class JoinGameMessage extends CommandMessage {
    public JoinGameMessage(String nickname, UUID uuid) {
        super(nickname, uuid);
    }
}
