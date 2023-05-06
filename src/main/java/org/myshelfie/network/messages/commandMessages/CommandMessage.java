package org.myshelfie.network.messages.commandMessages;

import java.util.UUID;

abstract public class CommandMessage {

    protected final String nickname;
    protected UUID gameUUID;

    public CommandMessage(String nickname) {
        this.nickname = nickname;
        this.gameUUID = null;
    }

    public CommandMessage(String nickname, UUID gameUUID) {
        this.nickname = nickname;
        this.gameUUID = gameUUID;
    }

    public String getNickname() {
        return nickname;
    }

    public UUID getGameUUID() {
        return gameUUID;
    }
}
