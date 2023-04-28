package org.myshelfie.network.server;

import org.myshelfie.model.Game;
import org.myshelfie.network.Listener;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.gameMessages.GameEventType;
import org.myshelfie.network.messages.gameMessages.GameView;

public class GameListener implements Listener<GameEventType> {
    private final Server server;
    private final Client client;
    private Game listenedGame;

    /**
     * This listener is responsible for reacting to changes in the model, thus it's concerned
     * in sending the modelView message via the {@link #update update()} method whenever a change occurs.
     * @param server The Server object that will send the message
     * @param client The Client that is interested in listening a Game
     * @param listenedGame The game that the client wants to listen to. (In view of implementing multi-game handling)
     */
    public GameListener(Server server, Client client, Game listenedGame) {
        this.server = server;
        this.client = client;
        this.listenedGame = listenedGame;
    }
    /**
     * Send to the client the (immutable) game after a change.
     *
     * @param ev  The event that has been emitted
     * @param arg In this case the argument is not used TODO: maybe it can be set as an optional parameter
     */
    @Override
    public void update(GameEventType ev, Object arg) {
        client.update(new GameView(this.listenedGame), ev);
    }
}
