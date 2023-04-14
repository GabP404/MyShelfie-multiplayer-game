package org.myshelfie.network.listener;

import org.myshelfie.network.Client;
import org.myshelfie.network.Listener;
import org.myshelfie.network.Server;
import org.myshelfie.network.messages.commandMessages.*;
import org.myshelfie.view.CommandLineInterface;

public class UserInputListener implements Listener<UserInputEventType> {
    private final Server server;
    private final Client client;

    public UserInputListener(Server server, Client client) {
        this.server = server;
        this.client = client;
    }

    /**
     * This method is called when user input is received from the CLI.
     * Creates the appropriate message, wraps it and sends it to the server.
     * @param ev The event that was fired. NOTE: This must be an element of an enumeration!
     * @param arg The arguments that were passed to the event.
     */
    @Override
    public void update(UserInputEventType ev, Object arg) {
        CommandLineInterface cli = (CommandLineInterface) arg;

        // TODO: define how to precisely retrieve the data from the cli
        CommandMessage m = switch (ev) {
            case SELECTED_TILES -> new PickedTilesCommandMessage(cli.getSelectedTiles());
            case SELECTED_BOOKSHELF_COLUMN -> new SelectedColumnMessage(cli.getSelectedColumn());
            case SELECTED_HAND_TILE -> new SelectedTileFromHandCommandMessage(cli.getSelectedTileFromHand());
            default ->
                // TODO: decide whether to throw an exception or send a special kind of message
                    null;
        };
        // send the message to the server
        server.update(client, new CommandMessageWrapper(m, ev));
    }
}
