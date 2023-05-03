package org.myshelfie.model;

public class WrongArgumentException extends Throwable{
    public WrongArgumentException(String message) {
        super(message);
    }
}