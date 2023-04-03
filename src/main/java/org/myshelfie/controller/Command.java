package org.myshelfie.controller;

import org.myshelfie.model.TileInsertionException;

public interface Command {
    void execute() throws InvalidCommand, TileInsertionException;
}
