package org.myshelfie.network.client;

import org.myshelfie.network.Listener;
import org.myshelfie.network.messages.commandMessages.UserInputEventType;
import org.myshelfie.network.server.Server;
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
     *
     * @param ev  The event that was fired. NOTE: This must be an element of an enumeration!
     * @param arg Message to be sent to the server
     */
    @Override
    public void update(UserInputEventType ev, Object arg) {
        CommandLineInterface cli = client.getCLI();
        /*
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

         */
    }
}
