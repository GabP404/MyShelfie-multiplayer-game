package org.myshelfie.network.messages.gameMessages;

/**
 * Enumeration of all the possible evevnts that can be generated
 * inside the {@link org.myshelfie.model model} and sent to the clients.
 */
public enum GameEvent {
    BOOKSHELF_UPDATE,
    BOARD_UPDATE,
    TOKEN_STACK_UPDATE,
    TOKEN_UPDATE,
    CURR_PLAYER_UPDATE,
    ERROR_STATE_RESET,
    PLAYER_ONLINE_UPDATE,
    TILES_PICKED_UPDATE,
    SELECTED_COLUMN_UPDATE,
    FINAL_TOKEN_UPDATE,
    GAME_END,
    ERROR
}
