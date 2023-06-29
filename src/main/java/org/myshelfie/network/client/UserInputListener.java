package org.myshelfie.network.client;

import org.myshelfie.controller.GameController;
import org.myshelfie.model.util.Pair;
import org.myshelfie.network.EventManager;
import org.myshelfie.network.Listener;
import org.myshelfie.network.messages.commandMessages.*;
import org.myshelfie.network.messages.gameMessages.EventWrapper;

import java.util.List;

/**
 * Listener responsible for events that are triggered by user input.
 * It refers to events of type {@link UserInputEvent}.
 */
public class UserInputListener implements Listener<UserInputEvent> {
    private final Client client;

    public UserInputListener(Client client) {
        this.client = client;
    }

    /**
     * This method is responsible for packaging and sending the appropriate message to the server.
     * The type of message is determined by the event that was fired.
     * This kind of listener handles the response directly after sending the message, which is directly sent
     * on every event (unlike {@link org.myshelfie.network.server.GameListener GameListener}).
     * @param ev  The event that was fired. NOTE: This must be an element of an enumeration
     * @param args Arguments included in the message sent to the server. The number and types
     *             of these parameters may vary based on the event the user generated.12
     */
    @Override
    public void update(UserInputEvent ev, Object... args) {
        switch (ev) {
            case NICKNAME -> {
                Pair<ConnectingStatuses, List<GameController.GameDefinition>> response = (Pair<ConnectingStatuses, List<GameController.GameDefinition>>) client.updateServerPreGame(
                        new CommandMessageWrapper(new NicknameMessage((String) args[0]), ev)
                );
                if (response.getLeft() == ConnectingStatuses.CONFIRMED) {
                    client.setNickname((String) args[0]);
                    client.startHeartBeatThread();
                    client.endLoginPhase(); // Stop the view thread that was waiting for the nickname
                    client.getView().setAvailableGames(response.getRight());
                    System.out.println("Successfully set nickname to " + args[0]);
                } else if (response.getLeft() == ConnectingStatuses.RECONNECTING) {
                    client.setNickname((String) args[0]);
                    client.startHeartBeatThread();
                    System.out.println("Successfully set nickname to " + args[0]);
                    client.getView().setReconnecting(true);
                    System.out.println("Reconnected to a game!");
                    client.endLoginPhase(); // Stop the view thread that was waiting for the nickname
                    client.startServerListener();
                } else {
                    client.getView().nicknameAlreadyUsed();
                }
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
                if (successfulCreation) {
                    System.out.println("Successfully created game " + args[0]);
                    client.endLobbyPhase();
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
                    client.endLobbyPhase();
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
                        client.endLobbyPhase();
                    }
                }

            }
            case REFRESH_AVAILABLE_GAMES -> {
                List<GameController.GameDefinition> games = (List<GameController.GameDefinition>) client.updateServerPreGame(new CommandMessageWrapper(
                        new RefreshAvailableGamesMessage(client.getNickname()), ev)
                );
                // FIXME: the actual view should be updated here and the System.out should be removed!
                client.getView().setAvailableGames(games);
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
