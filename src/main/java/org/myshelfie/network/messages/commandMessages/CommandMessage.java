package org.myshelfie.network.messages.commandMessages;

import java.io.Serializable;

/**
 * This abstract class is impemented by all the messages sent by the Client to the Server.
 * Together with the interface {@link org.myshelfie.controller.Command} a command pattern
 * is realized: the CommanMessages will be translated into Command objects and scheduled for the execution.
 */
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

    /**
     * @return The nickname of the client that generated this command message.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @return The name of the game this message is directed to.
     */
    public String getGameName() {
        return gameName;
    }
}
