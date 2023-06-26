package org.myshelfie.network.server;

import org.myshelfie.model.Board;
import org.myshelfie.model.Bookshelf;
import org.myshelfie.model.Game;
import org.myshelfie.model.Player;
import org.myshelfie.network.Listener;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.gameMessages.GameEvent;
import org.myshelfie.network.messages.gameMessages.EventWrapper;
import org.myshelfie.network.messages.gameMessages.GameView;

import java.net.Socket;
import java.rmi.RemoteException;
import java.util.logging.Level;

public class GameListener implements Listener<GameEvent> {
    private final Server server;
    private final Client client;
    private Game listenedGame;

    private GameEvent lastEvent;

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

    public void setListenedGame(Game listenedGame) {
        this.listenedGame = listenedGame;
    }

    /**
     * Save to the client the (immutable) game after a change.
     *
     * @param ev  The event that has been emitted
     */
    @Override
    public void update(GameEvent ev, Object... args) {
        if (this.listenedGame == null) {
            lastEvent = null;
            return; //The game hasn't been set yet
        }

        if (!isMyGameChanged(ev, args[0])) {
            lastEvent = null;
            return; // It's not my game the one that changed
        }

        lastEvent = ev;
        server.log(Level.FINE, "Generated event " + ev + " for client: " + client.getNickname());
    }

    /**
     * Send the last event to the client.
     */
    public void sendLastEvent() {
        if (lastEvent == null) {
            return; // No event to send
        }
        server.log(Level.FINE, "Sending event " + lastEvent + " to client: " + client.getNickname());

        GameView message = new GameView(listenedGame);
        if (client.isRMI()) {
            try {
                client.updateRMI(message, lastEvent);
            } catch (RemoteException e) {
                server.log(Level.SEVERE, "Error in updating the RMI client: " + e.getMessage());
            }
        } else {
            EventWrapper ew = new EventWrapper(message, lastEvent);
            Socket clientSocket = client.getClientSocket();
            server.sendTo(clientSocket, ew);
        }
    }

    /**
     * Check if the object that changed belongs to the game that this listener is listening to.
     * Thus, the checks are made on the object that triggered the event, and they depend on the type of event.
     * @param ev the event that has been emitted
     * @param changedItem the object that changed and triggered the event
     * @return true if the object that triggered the event belongs to the game that this listener is listening to.
     */
    private boolean isMyGameChanged(GameEvent ev, Object changedItem) {
        if (listenedGame == null || listenedGame.getPlayers() == null || listenedGame.getBoard() == null) {
            return false;
        }
        switch (ev) {
            case BOARD_UPDATE -> {
                // this kind of event is directly triggered by a Board object
                Board board = (Board) changedItem;
                return listenedGame.getBoard() == board;
            }
            case BOOKSHELF_UPDATE -> {
                // this kind of event is directly triggered by a Bookshelf object
                Bookshelf bookshelf = (Bookshelf) changedItem;
                return listenedGame.getPlayers().stream().anyMatch(p -> p.getBookshelf() == bookshelf);
            }
            case TOKEN_STACK_UPDATE, CURR_PLAYER_UPDATE, ERROR_STATE_RESET, ERROR, GAME_END -> {
                // all these events are directly triggered by a Game object
                Game game = (Game) changedItem;
                return listenedGame == game;
            }
            case TOKEN_UPDATE, PLAYER_ONLINE_UPDATE, TILES_PICKED_UPDATE, SELECTED_COLUMN_UPDATE, FINAL_TOKEN_UPDATE -> {
                // all these events are directly triggered by a Player object
                Player player = (Player) changedItem;
                return listenedGame.getPlayers().stream().anyMatch(p -> p == player);
            }
            default -> {
                throw new RuntimeException("Unexpected event: " + ev);
            }
        }
    }

    public Client getClient() {
        return client;
    }
}
