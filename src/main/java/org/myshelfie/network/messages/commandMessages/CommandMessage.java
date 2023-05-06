package org.myshelfie.network.messages.commandMessages;

abstract public class CommandMessage {

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
