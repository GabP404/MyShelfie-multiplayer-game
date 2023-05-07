package org.myshelfie.network.client;

import org.myshelfie.controller.GameController;
import org.myshelfie.network.Listener;
import org.myshelfie.network.messages.commandMessages.*;
import org.myshelfie.network.messages.gameMessages.EventWrapper;

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
     * @param args Contains different number and types of parameters depending on event type
     */
    @Override
    public void update(UserInputEvent ev, Object... args) {
        // TODO: handle in with a different update call to the server the case of a NICKNAME event
        switch (ev) {
            case NICKNAME -> {
                List<GameController.GameDefinition> games = (List<GameController.GameDefinition>) client.updateServerPreGame(
                        new CommandMessageWrapper(new NicknameMessage((String) args[0]), ev)
                );
                client.setNickname((String) args[0]);
                client.endNicknameThread(); // Stop the view thread that was waiting for the nickname
            }
            case CREATE_GAME -> {
                boolean successfulCreation = (boolean) client.updateServerPreGame(new CommandMessageWrapper(
                    new CreateGameMessage(
                            client.getNickname(),
                            (String) args[0],
                            (Integer) args[1],
                            (boolean) args[2]
                    ), ev)
                );
                // TODO do something on the view
                if (successfulCreation) {
                    System.out.println("Successfully created game " + args[0]);
                    client.endCreateGameThread();
                }
            }
            case JOIN_GAME -> {
                Object joinGameResponse = client.updateServerPreGame(new CommandMessageWrapper(
                        new JoinGameMessage(
                                client.getNickname(),
                                (String) args[0]
                        ), ev)
                );
                if (joinGameResponse instanceof EventWrapper) {
                    System.out.println(client.getNickname() + ": Successfully joined game " + args[0]);
                    client.endJoinGameThread();
                    System.out.println(client.getNickname() + ": Game started!");
                    // This response is the game view; the client should now update the view since the game
                    // has now started!
                    EventWrapper ew = (EventWrapper) joinGameResponse;
                    try {
                        client.update(ew.getMessage(), ew.getType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (joinGameResponse instanceof Boolean) {
                    // TODO do something on the view
                    if ((boolean) joinGameResponse) {
                        System.out.println("Successfully joined game " + args[0]);
                        client.endJoinGameThread();
                    }
                }

            }
            default -> {
                CommandMessage m = switch (ev) {
                    case SELECTED_TILES -> new PickedTilesCommandMessage(client.getNickname(), client.getGameName(), (List) args[0]);
                    case SELECTED_BOOKSHELF_COLUMN -> new SelectedColumnMessage(client.getNickname(), client.getGameName(), (Integer) args[0]);
                    // TODO: remove tileType (redundant)
                    case SELECTED_HAND_TILE -> new SelectedTileFromHandCommandMessage(client.getNickname(), client.getGameName(), (Integer) args[0], null);
                    default -> throw new RuntimeException("Unexpected value: " + ev);
                };
                // send the message to the server
                client.updateServer(new CommandMessageWrapper(m, ev));
            }
        }
    }
}
