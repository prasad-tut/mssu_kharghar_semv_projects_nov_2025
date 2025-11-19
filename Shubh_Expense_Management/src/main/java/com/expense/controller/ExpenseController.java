package com.expense.controller;

import com.expense.dto.ApprovalRequest;
import com.expense.dto.ExpenseRequest;
import com.expense.dto.ExpenseResponse;
import com.expense.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for expense management endpoints.
 * Handles CRUD operations for expenses with pagination, sorting, and filtering.
 */
@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@Slf4j
public class ExpenseController {
    
    private final ExpenseService expenseService;
    
    /**
     * Get all expenses for the authenticated user with pagination, sorting, and filtering.
     * Endpoint: GET /api/expenses
     *
     * @param authentication the current authentication object
     * @param page the page number (default: 0)
     * @param size the page size (default: 10)
     * @param sortBy the field to sort by (default: expenseDate)
     * @param sortDir the sort direction (default: desc)
     * @return ResponseEntity with Page of ExpenseResponse DTOs
     */
    @GetMapping
    public ResponseEntity<Page<ExpenseResponse>> getAllExpenses(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "expenseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        String userEmail = authentication.getName();
        log.info("Retrieving expenses for user: {} (page: {}, size: {}, sortBy: {}, sortDir: {})",
                userEmail, page, size, sortBy, sortDir);
        
        // Create sort object
        Sort sort = sortDir.equalsIgnoreCase("asc") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        
        // Create pageable object
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Fetch expenses
        Page<ExpenseResponse> expenses = expenseService.getAllExpensesForUser(userEmail, pageable);
        
        log.info("Retrieved {} expenses for user: {}", expenses.getTotalElements(), userEmail);
        return ResponseEntity.ok(expenses);
    }
    
    /**
     * Get a single expense by ID.
     * Endpoint: GET /api/expenses/{id}
     *
     * @param id the expense ID
     * @param authentication the current authentication object
     * @return ResponseEntity with ExpenseResponse DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getExpenseById(
            @PathVariable Long id,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        log.info("Retrieving expense with ID: {} for user: {}", id, userEmail);
        
        ExpenseResponse expense = expenseService.getExpenseById(id, userEmail);
        
        return ResponseEntity.ok(expense);
    }
    
    /**
     * Create a new expense.
     * Endpoint: POST /api/expenses
     *
     * @param expenseRequest the expense details
     * @param authentication the current authentication object
     * @return ResponseEntity with created ExpenseResponse DTO
     */
    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(
            @Valid @RequestBody ExpenseRequest expenseRequest,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        log.info("Creating expense for user: {}", userEmail);
        
        ExpenseResponse createdExpense = expenseService.createExpense(expenseRequest, userEmail);
        
        log.info("Expense created successfully with ID: {}", createdExpense.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExpense);
    }
    
    /**
     * Update an existing expense.
     * Endpoint: PUT /api/expenses/{id}
     *
     * @param id the expense ID
     * @param expenseRequest the updated expense details
     * @param authentication the current authentication object
     * @return ResponseEntity with updated ExpenseResponse DTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest expenseRequest,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        log.info("Updating expense with ID: {} for user: {}", id, userEmail);
        
        ExpenseResponse updatedExpense = expenseService.updateExpense(id, expenseRequest, userEmail);
        
        log.info("Expense updated successfully: {}", id);
        return ResponseEntity.ok(updatedExpense);
    }
    
    /**
     * Delete an expense.
     * Endpoint: DELETE /api/expenses/{id}
     *
     * @param id the expense ID
     * @param authentication the current authentication object
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long id,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        log.info("Deleting expense with ID: {} for user: {}", id, userEmail);
        
        expenseService.deleteExpense(id, userEmail);
        
        log.info("Expense deleted successfully: {}", id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Submit an expense for approval.
     * Endpoint: POST /api/expenses/{id}/submit
     *
     * @param id the expense ID
     * @param authentication the current authentication object
     * @return ResponseEntity with updated ExpenseResponse DTO
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<ExpenseResponse> submitExpense(
            @PathVariable Long id,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        log.info("Submitting expense with ID: {} for approval by user: {}", id, userEmail);
        
        ExpenseResponse submittedExpense = expenseService.submitExpenseForApproval(id, userEmail);
        
        log.info("Expense submitted successfully: {}", id);
        return ResponseEntity.ok(submittedExpense);
    }
    
    /**
     * Get all pending expenses for manager approval.
     * Endpoint: GET /api/expenses/pending
     * Only accessible by managers and admins.
     *
     * @param authentication the current authentication object
     * @return ResponseEntity with List of ExpenseResponse DTOs
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ExpenseResponse>> getPendingExpenses(
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        log.info("Retrieving pending expenses for manager: {}", userEmail);
        
        List<ExpenseResponse> pendingExpenses = expenseService.getPendingExpenses(userEmail);
        
        log.info("Retrieved {} pending expenses for manager: {}", pendingExpenses.size(), userEmail);
        return ResponseEntity.ok(pendingExpenses);
    }
    
    /**
     * Approve an expense.
     * Endpoint: POST /api/expenses/{id}/approve
     * Only accessible by managers and admins.
     *
     * @param id the expense ID
     * @param approvalRequest the approval request with optional review notes
     * @param authentication the current authentication object
     * @return ResponseEntity with updated ExpenseResponse DTO
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<ExpenseResponse> approveExpense(
            @PathVariable Long id,
            @Valid @RequestBody ApprovalRequest approvalRequest,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        log.info("Approving expense with ID: {} by manager: {}", id, userEmail);
        
        ExpenseResponse approvedExpense = expenseService.approveExpense(
                id, 
                approvalRequest.getReviewNotes(), 
                userEmail
        );
        
        log.info("Expense approved successfully: {}", id);
        return ResponseEntity.ok(approvedExpense);
    }
    
    /**
     * Reject an expense.
     * Endpoint: POST /api/expenses/{id}/reject
     * Only accessible by managers and admins.
     *
     * @param id the expense ID
     * @param approvalRequest the rejection request with optional review notes
     * @param authentication the current authentication object
     * @return ResponseEntity with updated ExpenseResponse DTO
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<ExpenseResponse> rejectExpense(
            @PathVariable Long id,
            @Valid @RequestBody ApprovalRequest approvalRequest,
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        log.info("Rejecting expense with ID: {} by manager: {}", id, userEmail);
        
        ExpenseResponse rejectedExpense = expenseService.rejectExpense(
                id, 
                approvalRequest.getReviewNotes(), 
                userEmail
        );
        
        log.info("Expense rejected successfully: {}", id);
        return ResponseEntity.ok(rejectedExpense);
    }
}
