package org.myshelfie.network.server;

import org.myshelfie.network.EventManager;
import org.myshelfie.network.Listener;
import org.myshelfie.network.messages.gameMessages.GameEvent;

import java.util.List;

/**
 * Customer EventManager for the server.
 * It adds the method sendToClients() that sends the last event to all the clients, since {@link GameListener#update}
 * does not send the event to the clients.
 * This is made in order to avoid sending the event to the client before the necessary checks are made.
 */
public class ServerEventManager extends EventManager {
    public <E extends Enum<E>> void sendToClients() {
        List<Listener<?>> eventListeners = listeners.get(GameEvent.class);
        if (eventListeners != null) {
            for (Listener<?> listener : eventListeners) {
                GameListener gameListener = (GameListener) listener;
                gameListener.sendLastEvent();
            }
        }
    }

    public <E extends Enum<E>> void notifyAndSendToClients(E event, Object... args) {
        // this method will probably join Silvio :(
        List<Listener<?>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (Listener<?> listener : eventListeners) {
                Listener<E> typedListener = (Listener<E>) listener;
                typedListener.update(event, args);
            }
        }
        sendToClients();
    }
}
