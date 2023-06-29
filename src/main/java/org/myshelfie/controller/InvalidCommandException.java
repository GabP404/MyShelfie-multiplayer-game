package org.myshelfie.controller;

/**
 * This exception is thrown when the command is not valid, for example when the game is not in the right state.
 */
public class InvalidCommandException extends Exception {
    public InvalidCommandException() {
        super();
    }
    public InvalidCommandException(String e) {
        super(e);
    }
}
