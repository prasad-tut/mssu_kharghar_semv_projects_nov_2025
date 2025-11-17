package com.coursemanagement.exception;

public class DuplicateCourseException extends RuntimeException {
    
    public DuplicateCourseException(String title) {
        super("Course with title '" + title + "' already exists");
    }
}
