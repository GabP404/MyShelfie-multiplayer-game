package org.myshelfie.controller;

import org.myshelfie.model.WrongArgumentException;

public interface Command {
    void execute() throws InvalidCommand, WrongTurnException, WrongArgumentException;
}
