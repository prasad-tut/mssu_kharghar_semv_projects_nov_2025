package com.expense.exception;

/**
 * Exception thrown when a user is authenticated but lacks permission to access a resource.
 * Results in HTTP 403 Forbidden response.
 * This is an alias for UnauthorizedException to match design document terminology.
 */
public class ForbiddenException extends RuntimeException {
    
    public ForbiddenException(String message) {
        super(message);
    }
}
