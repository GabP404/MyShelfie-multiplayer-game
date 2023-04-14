package org.myshelfie.network.local;

import org.myshelfie.network.Client;
import org.myshelfie.network.EventManager;
import org.myshelfie.network.Server;
import org.myshelfie.network.listener.UserInputListener;
import org.myshelfie.network.messages.commandMessages.UserInputEventType;
import org.myshelfie.network.messages.gameMessages.GameEventType;
import org.myshelfie.network.messages.gameMessages.GameView;
import org.myshelfie.view.CommandLineInterface;

public class ClientImpl implements Client, Runnable {
    // TODO: initialize the view (it needs to know the nickname)
    private CommandLineInterface view;
    public static EventManager eventManager = new EventManager();

    /**
     * Create a new client and register it to the server, establish a listening relationship between the view and the server
     * @param server the server the client registers to
     */
    public ClientImpl(Server server, String nickName) {
        view = new CommandLineInterface(nickName);
        server.register(this);
        // Subscribe a new UserInputListener that listen to changes in the view and forward events adding message to the server
        eventManager.subscribe(UserInputEventType.class, new UserInputListener(server, this));
    }

    /**
     * Called by the server to propagate the model change to the view
     * @param game    The resulting model view
     * @param event   The causing event
     */
    @Override
    public void update(GameView game, GameEventType event) {
        view.update(game, event);
    }

    /**
     * This method is needed for make the CLI available to the UserInputListener that will have to retrieve changes
     * made by the client on the view and send them to the server
     * @return The view of this client
     */
    @Override
    public CommandLineInterface getCLI() {
        return this.view;
    }

    @Override
    public void run() {
        view.run();
    }
}
