package org.myshelfie.controller;

public class WrongTurnException extends Throwable{
    public WrongTurnException() {
        super();
    }
    public WrongTurnException(String message) {
        super(message);
    }
}
