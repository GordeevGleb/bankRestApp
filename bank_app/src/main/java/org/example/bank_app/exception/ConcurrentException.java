package org.example.bank_app.exception;

public class ConcurrentException extends RuntimeException {
    public ConcurrentException(String message) {
        super(message);
    }
}
