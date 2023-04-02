package org.myshelfie.controller;

public interface Command {
    public void execute() throws InvalidCommand;
}
