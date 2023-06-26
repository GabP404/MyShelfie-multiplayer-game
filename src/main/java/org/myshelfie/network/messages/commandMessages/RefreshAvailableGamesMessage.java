package org.myshelfie.network.messages.commandMessages;

/**
 * This message is used to request the list of available games from the server, since
 * this list is not sent automatically except from the first time the client connects to the server.
 */
public class RefreshAvailableGamesMessage extends CommandMessage {
    public RefreshAvailableGamesMessage(String nickname) {
        super(nickname);
    }
}
