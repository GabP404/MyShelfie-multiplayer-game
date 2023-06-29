package org.myshelfie.network.messages.gameMessages;

import java.io.Serializable;

/**
 * This class wraps a message to be sent to the clients, including the updated {@link GameView} and the
 * {@link GameEvent event} that triggered the update.
 */
public class EventWrapper implements Serializable {
    private final GameEvent type;
    private final GameView message;

    /**
     * @param m Information to be sent
     * @param t Type of the information to be sent
     */
    public EventWrapper(GameView m, GameEvent t) {
        type = t;
        message = m;
    }

    /**
     * @return The type of event that triggered the update.
     */
    public GameEvent getType() {
            return type;
        }

    /**
      * @return The updated {@link GameView game view} to be sent to the clients.
     */
    public GameView getMessage() {
        return message;
    }
}
