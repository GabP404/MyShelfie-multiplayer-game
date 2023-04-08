package org.myshelfie.network.listener;

import org.myshelfie.model.Game;
import org.myshelfie.network.messages.gameMessages.GameEventType;
import org.myshelfie.network.messages.gameMessages.GameView;
import org.myshelfie.network.Client;
import org.myshelfie.network.Listener;
import org.myshelfie.network.Server;

public class GameListener implements Listener {
    private final Server server;
    private final Client client;

    public GameListener(Server server, Client client) {
        this.server = server;
        this.client = client;
    }

    /**
     * Send to the client the (immutable) game after a change.
     * @param ev The event that has been emitted
     * @param arg The updated game (model)
     */
    @Override
    public <E extends Enum<E>> void update(E ev, Object arg) {
        client.update(new GameView((Game) arg), (GameEventType) ev);
    }
}
