package org.example.bank_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class BankRestExceptionHandler {

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConcurrentException(ConcurrentException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(NotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleWrongOperationException(WrongOperationException e) {
        return Map.of("error", e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(BadCredentialsException.class)
    public Map<String, String> handleBadCredentials(BadCredentialsException ex) {
        return Map.of("error", ex.getMessage());
    }
}
