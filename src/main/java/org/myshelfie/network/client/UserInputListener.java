package org.myshelfie.network.client;

import org.myshelfie.network.Listener;
import org.myshelfie.network.messages.commandMessages.CommandMessage;
import org.myshelfie.network.messages.commandMessages.CommandMessageWrapper;
import org.myshelfie.network.messages.commandMessages.SelectedColumnMessage;
import org.myshelfie.network.messages.commandMessages.UserInputEvent;

public class UserInputListener implements Listener<UserInputEvent> {
    private final Client client;

    public UserInputListener(Client client) {
        this.client = client;
    }

    /**
     * This method is called when user input is received from the CLI.
     * Creates the appropriate message, wraps it and sends it to the server.
     *
     * @param ev  The event that was fired. NOTE: This must be an element of an enumeration!
     * @param args UNUSED
     */
    @Override
    public void update(UserInputEvent ev, Object... args) {
        // TODO: define how to precisely retrieve the data from the cli
        CommandMessage m = switch (ev) {
            //case SELECTED_TILES -> new PickedTilesCommandMessage(cli.getSelectedTiles());
            //case SELECTED_BOOKSHELF_COLUMN -> new SelectedColumnMessage(cli.getSelectedColumn());
            case SELECTED_BOOKSHELF_COLUMN -> new SelectedColumnMessage(client.getNickname(), client.getGameName(),  2); //DEMO MESSAGE
            //case SELECTED_HAND_TILE -> new SelectedTileFromHandCommandMessage(cli.getSelectedTileFromHand());
            default -> throw new RuntimeException("Unexpected value: " + ev);
        };
        // send the message to the server
        client.updateServer(new CommandMessageWrapper(m, ev));
    }
}
