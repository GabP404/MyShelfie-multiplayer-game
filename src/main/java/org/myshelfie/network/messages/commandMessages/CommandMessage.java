package org.myshelfie.network.messages.commandMessages;

import java.io.Serializable;

abstract public class CommandMessage implements Serializable {

    protected final String nickname;
    protected String gameName;

    public CommandMessage(String nickname) {
        this.nickname = nickname;
        this.gameName = null;
    }

    public CommandMessage(String nickname, String gameName) {
        this.nickname = nickname;
        this.gameName = gameName;
    }

    public String getNickname() {
        return nickname;
    }

    public String getGameName() {
        return gameName;
    }
}
