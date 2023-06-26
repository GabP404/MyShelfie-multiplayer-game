package org.myshelfie.network;

/**
 * Generic Listener interface. Together with {@link EventManager} this class allow to any class
 * to trigger events by calling {@link EventManager#notify()}, which will be caught by every listener
 * interested to the class of events T.
 * @param <T> The enumeration of possible events that this listener will listen to
 */
public interface Listener<T extends Enum<T>> {
    /**
     * This method is called from the {@link EventManager#notify()} when a noification occurs.
     * Its implementation is responsible for the
     * @param ev  The event that was fired. NOTE: This must be an element of an enumeration
     * @param args Contains different number and types of parameters depending on event type
     */
    void update(T ev, Object... args);
}
