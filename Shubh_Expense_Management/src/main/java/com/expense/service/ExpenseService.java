package com.expense.service;

import com.expense.dto.CategoryResponse;
import com.expense.dto.ExpenseRequest;
import com.expense.dto.ExpenseResponse;
import com.expense.dto.ReceiptResponse;
import com.expense.dto.UserResponse;
import com.expense.exception.ResourceNotFoundException;
import com.expense.exception.UnauthorizedException;
import com.expense.model.Category;
import com.expense.model.Expense;
import com.expense.model.ExpenseStatus;
import com.expense.model.User;
import com.expense.repository.CategoryRepository;
import com.expense.repository.ExpenseRepository;
import com.expense.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing expense operations.
 * Handles CRUD operations, validation, and authorization for expenses.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseService {
    
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    
    /**
     * Create a new expense for the authenticated user.
     * Validates that the category exists and amount is positive.
     *
     * @param expenseRequest the expense details
     * @param userEmail the email of the authenticated user
     * @return ExpenseResponse containing the created expense
     * @throws ResourceNotFoundException if user or category not found
     * @throws IllegalArgumentException if validation fails
     */
    @Transactional
    public ExpenseResponse createExpense(ExpenseRequest expenseRequest, String userEmail) {
        log.info("Creating expense for user: {}", userEmail);
        
        // Fetch user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        // Validate and fetch category
        Category category = categoryRepository.findById(expenseRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", expenseRequest.getCategoryId()));
        
        // Create expense entity
        Expense expense = new Expense();
        expense.setUser(user);
        expense.setCategory(category);
        expense.setAmount(expenseRequest.getAmount());
        expense.setExpenseDate(expenseRequest.getExpenseDate());
        expense.setDescription(expenseRequest.getDescription());
        expense.setStatus(ExpenseStatus.DRAFT);
        
        // Save expense
        Expense savedExpense = expenseRepository.save(expense);
        log.info("Expense created successfully with ID: {}", savedExpense.getId());
        
        return mapToExpenseResponse(savedExpense);
    }
    
    /**
     * Retrieve all expenses for the authenticated user with pagination.
     *
     * @param userEmail the email of the authenticated user
     * @param pageable pagination and sorting parameters
     * @return Page of ExpenseResponse DTOs
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getAllExpensesForUser(String userEmail, Pageable pageable) {
        log.info("Retrieving expenses for user: {}", userEmail);
        
        // Fetch user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        // Fetch expenses with pagination
        Page<Expense> expenses = expenseRepository.findByUserId(user.getId(), pageable);
        
        log.info("Retrieved {} expenses for user: {}", expenses.getTotalElements(), userEmail);
        return expenses.map(this::mapToExpenseResponse);
    }
    
    /**
     * Retrieve a single expense by ID with authorization check.
     * Ensures the expense belongs to the authenticated user.
     *
     * @param expenseId the ID of the expense to retrieve
     * @param userEmail the email of the authenticated user
     * @return ExpenseResponse containing the expense details
     * @throws ResourceNotFoundException if expense not found
     * @throws UnauthorizedException if user doesn't own the expense
     */
    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(Long expenseId, String userEmail) {
        log.info("Retrieving expense with ID: {} for user: {}", expenseId, userEmail);
        
        // Fetch expense
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
        
        // Authorization check: ensure user owns the expense
        if (!expense.getUser().getEmail().equals(userEmail)) {
            log.warn("Unauthorized access attempt: User {} tried to access expense {} owned by {}",
                    userEmail, expenseId, expense.getUser().getEmail());
            throw new UnauthorizedException("You are not authorized to access this expense");
        }
        
        log.info("Expense retrieved successfully: {}", expenseId);
        return mapToExpenseResponse(expense);
    }
    
    /**
     * Update an existing expense with validation and authorization.
     * Ensures the expense belongs to the authenticated user and validates the new data.
     *
     * @param expenseId the ID of the expense to update
     * @param expenseRequest the updated expense details
     * @param userEmail the email of the authenticated user
     * @return ExpenseResponse containing the updated expense
     * @throws ResourceNotFoundException if expense or category not found
     * @throws UnauthorizedException if user doesn't own the expense
     * @throws IllegalArgumentException if expense is not in DRAFT status
     */
    @Transactional
    public ExpenseResponse updateExpense(Long expenseId, ExpenseRequest expenseRequest, String userEmail) {
        log.info("Updating expense with ID: {} for user: {}", expenseId, userEmail);
        
        // Fetch expense
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
        
        // Authorization check: ensure user owns the expense
        if (!expense.getUser().getEmail().equals(userEmail)) {
            log.warn("Unauthorized update attempt: User {} tried to update expense {} owned by {}",
                    userEmail, expenseId, expense.getUser().getEmail());
            throw new UnauthorizedException("You are not authorized to update this expense");
        }
        
        // Business rule: only DRAFT expenses can be updated
        if (expense.getStatus() != ExpenseStatus.DRAFT) {
            log.warn("Attempt to update non-draft expense: {} with status {}", expenseId, expense.getStatus());
            throw new IllegalArgumentException("Only expenses in DRAFT status can be updated");
        }
        
        // Validate and fetch category
        Category category = categoryRepository.findById(expenseRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", expenseRequest.getCategoryId()));
        
        // Update expense fields
        expense.setCategory(category);
        expense.setAmount(expenseRequest.getAmount());
        expense.setExpenseDate(expenseRequest.getExpenseDate());
        expense.setDescription(expenseRequest.getDescription());
        
        // Save updated expense
        Expense updatedExpense = expenseRepository.save(expense);
        log.info("Expense updated successfully: {}", expenseId);
        
        return mapToExpenseResponse(updatedExpense);
    }
    
    /**
     * Delete an expense with authorization check.
     * Ensures the expense belongs to the authenticated user.
     *
     * @param expenseId the ID of the expense to delete
     * @param userEmail the email of the authenticated user
     * @throws ResourceNotFoundException if expense not found
     * @throws UnauthorizedException if user doesn't own the expense
     * @throws IllegalArgumentException if expense is not in DRAFT status
     */
    @Transactional
    public void deleteExpense(Long expenseId, String userEmail) {
        log.info("Deleting expense with ID: {} for user: {}", expenseId, userEmail);
        
        // Fetch expense
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
        
        // Authorization check: ensure user owns the expense
        if (!expense.getUser().getEmail().equals(userEmail)) {
            log.warn("Unauthorized delete attempt: User {} tried to delete expense {} owned by {}",
                    userEmail, expenseId, expense.getUser().getEmail());
            throw new UnauthorizedException("You are not authorized to delete this expense");
        }
        
        // Business rule: only DRAFT expenses can be deleted
        if (expense.getStatus() != ExpenseStatus.DRAFT) {
            log.warn("Attempt to delete non-draft expense: {} with status {}", expenseId, expense.getStatus());
            throw new IllegalArgumentException("Only expenses in DRAFT status can be deleted");
        }
        
        // Delete expense
        expenseRepository.delete(expense);
        log.info("Expense deleted successfully: {}", expenseId);
    }
    
    /**
     * Submit an expense for approval.
     * Changes the expense status from DRAFT to SUBMITTED.
     *
     * @param expenseId the ID of the expense to submit
     * @param userEmail the email of the authenticated user
     * @return ExpenseResponse containing the submitted expense
     * @throws ResourceNotFoundException if expense not found
     * @throws UnauthorizedException if user doesn't own the expense
     * @throws IllegalArgumentException if expense is not in DRAFT status
     */
    @Transactional
    public ExpenseResponse submitExpenseForApproval(Long expenseId, String userEmail) {
        log.info("Submitting expense with ID: {} for approval by user: {}", expenseId, userEmail);
        
        // Fetch expense
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
        
        // Authorization check: ensure user owns the expense
        if (!expense.getUser().getEmail().equals(userEmail)) {
            log.warn("Unauthorized submit attempt: User {} tried to submit expense {} owned by {}",
                    userEmail, expenseId, expense.getUser().getEmail());
            throw new UnauthorizedException("You are not authorized to submit this expense");
        }
        
        // Business rule: only DRAFT expenses can be submitted
        if (expense.getStatus() != ExpenseStatus.DRAFT) {
            log.warn("Attempt to submit non-draft expense: {} with status {}", expenseId, expense.getStatus());
            throw new IllegalArgumentException("Only expenses in DRAFT status can be submitted");
        }
        
        // Update status and timestamp
        expense.setStatus(ExpenseStatus.SUBMITTED);
        expense.setSubmittedAt(java.time.LocalDateTime.now());
        
        // Save updated expense
        Expense submittedExpense = expenseRepository.save(expense);
        log.info("Expense submitted successfully: {}", expenseId);
        
        return mapToExpenseResponse(submittedExpense);
    }
    
    /**
     * Retrieve all pending expenses for manager approval.
     * Only accessible by users with MANAGER or ADMIN role.
     *
     * @param userEmail the email of the authenticated user (must be manager)
     * @return List of ExpenseResponse DTOs with SUBMITTED status
     * @throws ResourceNotFoundException if user not found
     * @throws UnauthorizedException if user is not a manager
     */
    @Transactional(readOnly = true)
    public java.util.List<ExpenseResponse> getPendingExpenses(String userEmail) {
        log.info("Retrieving pending expenses for manager: {}", userEmail);
        
        // Fetch user and verify manager role
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        if (user.getRole() != com.expense.model.UserRole.MANAGER && 
            user.getRole() != com.expense.model.UserRole.ADMIN) {
            log.warn("Unauthorized access attempt: User {} with role {} tried to access pending expenses",
                    userEmail, user.getRole());
            throw new UnauthorizedException("Only managers can access pending expenses");
        }
        
        // Fetch all expenses with SUBMITTED status
        java.util.List<Expense> pendingExpenses = expenseRepository.findByStatus(ExpenseStatus.SUBMITTED);
        
        log.info("Retrieved {} pending expenses for manager: {}", pendingExpenses.size(), userEmail);
        return pendingExpenses.stream()
                .map(this::mapToExpenseResponse)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Approve an expense.
     * Changes the expense status to APPROVED and records the reviewer.
     * Only accessible by users with MANAGER or ADMIN role.
     *
     * @param expenseId the ID of the expense to approve
     * @param reviewNotes optional notes from the reviewer
     * @param userEmail the email of the authenticated user (must be manager)
     * @return ExpenseResponse containing the approved expense
     * @throws ResourceNotFoundException if expense or user not found
     * @throws UnauthorizedException if user is not a manager
     * @throws IllegalArgumentException if expense is not in SUBMITTED status
     */
    @Transactional
    public ExpenseResponse approveExpense(Long expenseId, String reviewNotes, String userEmail) {
        log.info("Approving expense with ID: {} by manager: {}", expenseId, userEmail);
        
        // Fetch user and verify manager role
        User manager = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        if (manager.getRole() != com.expense.model.UserRole.MANAGER && 
            manager.getRole() != com.expense.model.UserRole.ADMIN) {
            log.warn("Unauthorized approval attempt: User {} with role {} tried to approve expense",
                    userEmail, manager.getRole());
            throw new UnauthorizedException("Only managers can approve expenses");
        }
        
        // Fetch expense
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
        
        // Business rule: only SUBMITTED expenses can be approved
        if (expense.getStatus() != ExpenseStatus.SUBMITTED) {
            log.warn("Attempt to approve expense with invalid status: {} has status {}", 
                    expenseId, expense.getStatus());
            throw new IllegalArgumentException("Only expenses in SUBMITTED status can be approved");
        }
        
        // Update expense with approval details
        expense.setStatus(ExpenseStatus.APPROVED);
        expense.setReviewedAt(java.time.LocalDateTime.now());
        expense.setReviewedBy(manager);
        expense.setReviewNotes(reviewNotes);
        
        // Save updated expense
        Expense approvedExpense = expenseRepository.save(expense);
        log.info("Expense approved successfully: {} by manager: {}", expenseId, userEmail);
        
        return mapToExpenseResponse(approvedExpense);
    }
    
    /**
     * Reject an expense.
     * Changes the expense status to REJECTED and records the reviewer.
     * Only accessible by users with MANAGER or ADMIN role.
     *
     * @param expenseId the ID of the expense to reject
     * @param reviewNotes optional notes from the reviewer
     * @param userEmail the email of the authenticated user (must be manager)
     * @return ExpenseResponse containing the rejected expense
     * @throws ResourceNotFoundException if expense or user not found
     * @throws UnauthorizedException if user is not a manager
     * @throws IllegalArgumentException if expense is not in SUBMITTED status
     */
    @Transactional
    public ExpenseResponse rejectExpense(Long expenseId, String reviewNotes, String userEmail) {
        log.info("Rejecting expense with ID: {} by manager: {}", expenseId, userEmail);
        
        // Fetch user and verify manager role
        User manager = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        if (manager.getRole() != com.expense.model.UserRole.MANAGER && 
            manager.getRole() != com.expense.model.UserRole.ADMIN) {
            log.warn("Unauthorized rejection attempt: User {} with role {} tried to reject expense",
                    userEmail, manager.getRole());
            throw new UnauthorizedException("Only managers can reject expenses");
        }
        
        // Fetch expense
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
        
        // Business rule: only SUBMITTED expenses can be rejected
        if (expense.getStatus() != ExpenseStatus.SUBMITTED) {
            log.warn("Attempt to reject expense with invalid status: {} has status {}", 
                    expenseId, expense.getStatus());
            throw new IllegalArgumentException("Only expenses in SUBMITTED status can be rejected");
        }
        
        // Update expense with rejection details
        expense.setStatus(ExpenseStatus.REJECTED);
        expense.setReviewedAt(java.time.LocalDateTime.now());
        expense.setReviewedBy(manager);
        expense.setReviewNotes(reviewNotes);
        
        // Save updated expense
        Expense rejectedExpense = expenseRepository.save(expense);
        log.info("Expense rejected successfully: {} by manager: {}", expenseId, userEmail);
        
        return mapToExpenseResponse(rejectedExpense);
    }
    
    /**
     * Map Expense entity to ExpenseResponse DTO.
     *
     * @param expense the Expense entity
     * @return ExpenseResponse DTO
     */
    private ExpenseResponse mapToExpenseResponse(Expense expense) {
        CategoryResponse categoryResponse = new CategoryResponse(
                expense.getCategory().getId(),
                expense.getCategory().getName(),
                expense.getCategory().getDescription()
        );
        
        ReceiptResponse receiptResponse = null;
        if (expense.getReceipt() != null) {
            receiptResponse = new ReceiptResponse(
                    expense.getReceipt().getId(),
                    expense.getReceipt().getFileName(),
                    expense.getReceipt().getFileType(),
                    expense.getReceipt().getFileSize(),
                    expense.getReceipt().getUploadedAt()
            );
        }
        
        UserResponse reviewedByResponse = null;
        if (expense.getReviewedBy() != null) {
            reviewedByResponse = new UserResponse(
                    expense.getReviewedBy().getId(),
                    expense.getReviewedBy().getEmail(),
                    expense.getReviewedBy().getFirstName(),
                    expense.getReviewedBy().getLastName(),
                    expense.getReviewedBy().getRole()
            );
        }
        
        return new ExpenseResponse(
                expense.getId(),
                categoryResponse,
                expense.getAmount(),
                expense.getExpenseDate(),
                expense.getDescription(),
                expense.getStatus(),
                receiptResponse,
                expense.getSubmittedAt(),
                expense.getReviewedAt(),
                reviewedByResponse,
                expense.getReviewNotes(),
                expense.getCreatedAt(),
                expense.getUpdatedAt()
        );
    }
}
