package org.myshelfie.network.messages.commandMessages;

import java.io.Serializable;

/**
 * List of all the types of messages that can be sent from the client to the server.
 */
public enum UserInputEvent implements Serializable {
    /**
     * List of the selected tiles to take from the living room board
     */
    SELECTED_TILES,
    /**
     * Chosen column of the bookshelf in which to put the chosen tile
     */
    SELECTED_BOOKSHELF_COLUMN,
    /**
     * Identifier of the tile in the player hand
     */
    SELECTED_HAND_TILE,
    /**
     * Message to create a new game
     */
    CREATE_GAME,
    /**
     * Message to join an existing game
     */
    JOIN_GAME,
    /**
     * Message to send the nickname of the player
     */
    NICKNAME
}
