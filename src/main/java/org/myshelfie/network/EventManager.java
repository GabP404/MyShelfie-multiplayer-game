package org.myshelfie.network;

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
    // Map that links the Class Object representing the type of the event (enum), to the list of listeners that will be able to handle it
    private Map<Class<? extends Enum<?>>, List<Listener<? extends Enum<?>>>> listeners = new HashMap<>();

    /**
     * Subscribe a listener to an event.
     * @param eventType The enum containing all the events this listener will listen to
     * @param listener The listener to subscribe
     */
    public <E extends Enum<E>> void subscribe(Class<E> eventType, Listener<E> listener) {
        if (!listeners.containsKey(eventType)) {
            listeners.put(eventType, new ArrayList<>());
        }
        List<Listener<?>> users = listeners.get(eventType);
        users.add(listener);
    }

    /**
     * Unsubscribe a listener from an event.
     * @param eventType The event to unsubscribe from
     * @param listener The listener to unsubscribe
     */
    public <E extends Enum<E>> void unsubscribe(Class<E> eventType, Listener<E> listener) {
        List<Listener<?>> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    /**
     * Notify all the listeners of the event and forward the argument.
     * @param event The event that has been emitted
     * @param arg The argument attached to the event
     */
    public <E extends Enum<E>> void notify(E event, Object arg) {
        List<Listener<?>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (Listener<?> listener : eventListeners) {
                // TODO: try to avoid this cast if possible
                Listener<E> typedListener = (Listener<E>) listener;
                typedListener.update(event, arg);
            }
        }
    }
}