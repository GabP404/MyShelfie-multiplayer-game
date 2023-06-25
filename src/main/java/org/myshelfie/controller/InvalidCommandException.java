package org.myshelfie.controller;

public class InvalidCommandException extends Exception {
    public InvalidCommandException() {
        super();
    }
    public InvalidCommandException(String e) {
        super(e);
    }
}
