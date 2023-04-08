package org.myshelfie.network.listener;

import org.myshelfie.network.Client;
import org.myshelfie.network.Listener;
import org.myshelfie.network.Server;
import org.myshelfie.network.messages.commandMessages.*;
import org.myshelfie.view.CommandLineInterface;

public class UserListener implements Listener {
    private final Server server;
    private final Client client;

    public UserListener(Server server, Client client) {
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
    public <E extends Enum<E>> void update(E ev, Object arg) {
        CommandLineInterface cli = (CommandLineInterface) arg;
        CommandMessageType event = (CommandMessageType) ev;

        // TODO: define how to precisely retrieve the data from the cli
        CommandMessage m = switch (event) {
            case SELECTED_TILES -> new PickedTilesCommandMessage(cli.getSelectedTiles());
            case SELECTED_BOOKSHELF_COLUMN -> new SelectedColumnMessage(cli.getSelectedColumn());
            case SELECTED_HAND_TILE -> new SelectedTileFromHandCommandMessage(cli.getSelectedTileFromHand());
            default ->
                // TODO: decide wheter to throw an exception or send a special kind of message
                    null;
        };
        // send the message to the server
        server.update(client, new CommandMessageWrapper(m, event));
    }
}
