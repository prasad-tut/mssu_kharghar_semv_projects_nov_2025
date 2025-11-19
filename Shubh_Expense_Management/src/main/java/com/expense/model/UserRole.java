package com.expense.model;

/**
 * Enum representing user roles in the Expense Management System.
 * Defines the authorization levels for different types of users.
 */
public enum UserRole {
    /**
     * Regular user who can create and manage their own expenses
     */
    USER,
    
    /**
     * Manager who can approve or reject submitted expenses
     */
    MANAGER,
    
    /**
     * Administrator with full system access
     */
    ADMIN
}
