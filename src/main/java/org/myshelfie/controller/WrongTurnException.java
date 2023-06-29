package org.myshelfie.controller;

/**
 * This exception is thrown when the player tries to perform an action when it's not their turn.
 */
public class WrongTurnException extends Throwable{
    public WrongTurnException() {
        super();
    }
    public WrongTurnException(String message) {
        super(message);
    }
}
