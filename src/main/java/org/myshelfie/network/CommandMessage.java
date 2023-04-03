package org.myshelfie.network;

abstract public class CommandMessage {

    protected final String nickname;

    public CommandMessage(String nickname) {
        this.nickname = nickname;
    }
}
