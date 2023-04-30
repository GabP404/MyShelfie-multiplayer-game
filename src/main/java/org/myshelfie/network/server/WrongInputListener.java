package org.myshelfie.network.server;

import org.myshelfie.network.Listener;
import org.myshelfie.network.client.Client;
import org.myshelfie.network.messages.gameMessages.WrongInputEvent;

public class WrongInputListener implements Listener<WrongInputEvent> {
    private final Server server;
    private final Client client;

    /**
     * This listener is reponsible for sending a message to the client that has sent a wrong message
     * or tried to perform a wrong action.
     * @param server The Server object that will send the message
     * @param client The Client that is interested in listening to wrong user input events
     */
    public WrongInputListener(Server server, Client client) {
        this.server = server;
        this.client = client;
    }

    /**
     * This method is called whenever the controller finds a message that is not well-composed or
     * expresses a wrong action.
     * @param ev event that has been triggered
     * @param arg argument contains the nickname of the client that has sent the wrong message
     *            (and maybe in future also the message itself)
     */
    @Override
    public void update(WrongInputEvent ev, Object arg) {
        // Check if
        String responsibleClient = (String) arg;
        if (responsibleClient.equals(client.getNickname())) {
            // send message to the client
            if (client.isRMI()) {
                client.update(null, ev);
            } else {
                // send message via socket
            }
        }
    }
}
