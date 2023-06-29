package org.myshelfie.network.messages.commandMessages;

/**
 * With this message a {@link org.myshelfie.network.client.Client} communicates
 * the will to join the game {@link CommandMessage#gameName gameName}
 */
public class JoinGameMessage extends CommandMessage {
    public JoinGameMessage(String nickname, String gameName) {
        super(nickname, gameName);
    }

}
