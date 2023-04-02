package org.myshelfie.network;

/**
 * List of all the types of messages that can be sent from the client to the server.
 */
public enum CommandMessageType {
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
     * Message to be sent to the chat
     */
    CHAT_MESSAGE
}
