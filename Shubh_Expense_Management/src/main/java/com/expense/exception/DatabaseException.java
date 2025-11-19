package com.expense.exception;

/**
 * Exception thrown when database operations fail.
 * Results in HTTP 500 Internal Server Error response.
 */
public class DatabaseException extends RuntimeException {
    
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
