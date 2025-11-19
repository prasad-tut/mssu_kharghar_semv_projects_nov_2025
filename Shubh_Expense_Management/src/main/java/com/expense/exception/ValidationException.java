package com.expense.exception;

/**
 * Exception thrown when business validation rules are violated.
 * Results in HTTP 400 Bad Request response.
 */
public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
