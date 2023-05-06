package org.myshelfie.controller;

public class InvalidCommand extends Exception {
    public InvalidCommand() {
        super();
    }
    public InvalidCommand(String e) {
        super(e);
    }
}
