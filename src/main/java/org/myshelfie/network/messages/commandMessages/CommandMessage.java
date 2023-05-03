package org.myshelfie.network.messages.commandMessages;

abstract public class CommandMessage {

    protected final String nickname;

    public CommandMessage(String nickname) {
        this.nickname = nickname;
    }
}
