package org.example.bank_app.exception;

public class WrongOperationException extends RuntimeException {
    public WrongOperationException(String message) {
        super(message);
    }
}
