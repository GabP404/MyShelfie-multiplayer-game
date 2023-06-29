package org.myshelfie.network.messages.commandMessages;

/**
 * Enumeration of possible connecting statuses. This is used in {@link org.myshelfie.network.client.UserInputListener}
 * to disinguish the possilbe Server's responses when connecting/reconnecting.
 */
public enum ConnectingStatuses {
    CONFIRMED,
    ERROR,
    RECONNECTING
}
