package org.myshelfie.network.client;

import org.myshelfie.network.EventManager;
import org.myshelfie.network.messages.gameMessages.ServerEvent;
import org.myshelfie.network.server.Server;
import org.myshelfie.network.messages.commandMessages.UserInputEvent;
import org.myshelfie.network.messages.gameMessages.GameView;
import org.myshelfie.view.CommandLineInterface;

public class ClientImpl extends Client implements Runnable {
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
        eventManager.subscribe(UserInputEvent.class, new UserInputListener(server, this));
    }

    /**
     * Called by the server to propagate the model change to the view
     * @param game  The resulting model view
     * @param event The causing event
     */
    @Override
    public void update(GameView game, ServerEvent event) {
        view.update(game, event);
    }

    @Override
    public void run() {
        view.run();
    }
}
