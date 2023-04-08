package org.myshelfie.network;

import org.myshelfie.network.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  This class is used to manage the listeners and the events.
 *  It stores a map that links the eventType to the listeners.
 *  When an event is generated, notify() will be called passing
 *  the event (of one of the specified eventType) and some argument.
 */
public class EventManager {
    private Map<String, List<Listener>> listeners = new HashMap<>();

    /**
     * Subscribe a listener to an event.
     * @param eventType The enum containing all the events this listener will listen to
     * @param listener The listener to subscribe
     */
    public void subscribe(Class<? extends Enum<?>> eventType, Listener listener) {
        String s = eventType.toString();
        if (!listeners.containsKey(s)) {
            listeners.put(s, new ArrayList<>());
        }
        List<Listener> users = listeners.get(s);
        users.add(listener);
    }

    /**
     * Unsubscribe a listener from an event.
     * @param eventType The event to unsubscribe from
     * @param listener The listener to unsubscribe
     */
    public void unsubscribe(Class<? extends Enum<?>> eventType, Listener listener) {
        List<Listener> users = listeners.get(eventType.toString());
        users.remove(listener);
    }

    /**
     * Notify all the listeners of the event and forward the argument.
     * @param event The event that has been emitted
     * @param arg The argument attached to the event
     */
    public <E extends Enum<E>> void notify(E event, Object arg) {
        // the string is the name of the enum that contains the event received
        String eventType = event.getDeclaringClass().toString();
        // The update() is called on all the listeners subscribed to the eventType
        List<Listener> users = listeners.get(eventType);
        for (Listener listener : users) {
            listener.update(event, arg);
        }
    }
}