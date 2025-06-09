package org.example.bank_app.exception;

public class ServiceAuthenticationException extends RuntimeException {
    public ServiceAuthenticationException(String message) {
        super(message);
    }
}
