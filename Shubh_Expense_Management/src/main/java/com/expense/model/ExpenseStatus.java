package com.expense.model;

/**
 * Enum representing the lifecycle status of an expense record.
 * Tracks the approval workflow state of expenses.
 */
public enum ExpenseStatus {
    /**
     * Expense is being created or edited, not yet submitted
     */
    DRAFT,
    
    /**
     * Expense has been submitted for manager approval
     */
    SUBMITTED,
    
    /**
     * Expense has been approved by a manager
     */
    APPROVED,
    
    /**
     * Expense has been rejected by a manager
     */
    REJECTED
}
