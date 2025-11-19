package com.expense.exception;

/**
 * Exception thrown when a user attempts to access a resource they don't have permission to access.
 * Results in HTTP 403 Forbidden response.
 */
public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
}
