package org.myshelfie.network.client;

import org.myshelfie.network.messages.gameMessages.GameView;
import org.myshelfie.network.messages.gameMessages.ServerEvent;
import org.myshelfie.view.CommandLineInterface;

public abstract class Client {
    protected String nickname;
    protected boolean isRMI;
    private CommandLineInterface view;
    /**
     * Called by the server to propagate the model change to the view
     * @param gameView The resulting model view
     * @param event    The causing event
     */
    public abstract void update(GameView gameView, ServerEvent event);

    /**
     * This method is needed for make the CLI available to the UserInputListener that will have to retrieve changes
     * made by the client on the view and send them to the server
     * @return The view of this client
     */
    CommandLineInterface getCLI() {
        return this.view;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isRMI() {
        return isRMI;
    }
}
