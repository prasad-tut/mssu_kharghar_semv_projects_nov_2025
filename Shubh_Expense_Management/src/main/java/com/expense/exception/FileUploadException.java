package com.expense.exception;

/**
 * Exception thrown when file upload operations fail.
 * Used for validation errors, storage errors, or file processing issues.
 */
public class FileUploadException extends RuntimeException {
    
    public FileUploadException(String message) {
        super(message);
    }
    
    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
