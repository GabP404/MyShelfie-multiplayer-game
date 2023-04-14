package org.myshelfie.network.listener;

import org.myshelfie.network.Client;
import org.myshelfie.network.Listener;
import org.myshelfie.network.Server;
import org.myshelfie.network.messages.gameMessages.GameEventType;
import org.myshelfie.network.messages.gameMessages.GameView;

public class GameListener implements Listener<GameEventType> {
    private final Server server;
    private final Client client;

    public GameListener(Server server, Client client) {
        this.server = server;
        this.client = client;
    }
    /**
     * Send to the client the (immutable) game after a change.
     * @param ev The event that has been emitted
     */
    @Override
    public void update(GameEventType ev) {
        client.update(new GameView(server.getGame()), ev);
    }
}
