package org.myshelfie.controller;

public class WrongTurnException extends Throwable{
    public WrongTurnException(String message) {
        super(message);
    }
}
