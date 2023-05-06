package org.myshelfie.network.client;

import org.myshelfie.network.Listener;
import org.myshelfie.network.messages.commandMessages.*;

import java.util.List;

public class UserInputListener implements Listener<UserInputEvent> {
    private final Client client;

    public UserInputListener(Client client) {
        this.client = client;
    }

    /**
     * This method is called when user input is received from the CLI.
     * Creates the appropriate message, wraps it and sends it to the server.
     * @param ev  The event that was fired. NOTE: This must be an element of an enumeration!
     * @param args UNUSED
     */
    @Override
    public void update(UserInputEvent ev, Object... args) {
        // TODO: handle in with a different update call to the server the case of a NICKNAME event

        CommandMessage m = switch (ev) {
            case SELECTED_TILES -> new PickedTilesCommandMessage(client.getNickname(), (List) args[0]);
            case SELECTED_BOOKSHELF_COLUMN -> new SelectedColumnMessage(client.getNickname(), (Integer) args[0]);
            // TODO: remove tileType (redundant)
            case SELECTED_HAND_TILE -> new SelectedTileFromHandCommandMessage(client.getNickname(), (Integer) args[0], null);
            default -> throw new RuntimeException("Unexpected value: " + ev);
        };
        // send the message to the server
        client.updateServer(new CommandMessageWrapper(m, ev));
    }
}
