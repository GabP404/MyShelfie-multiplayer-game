package org.myshelfie.controller;

import org.myshelfie.model.WrongArgumentException;

/**
 * Interface used to implement a command pattern.
 */
public interface Command {
    /**
     * Only method to implement a command. This is called by the controller to execute the command.
     * @throws InvalidCommandException If the command is not valid
     * @throws WrongTurnException If the command is requested in the wrong turn
     * @throws WrongArgumentException If the command is requested with wrong arguments
     */
    void execute() throws InvalidCommandException, WrongTurnException, WrongArgumentException;
}
