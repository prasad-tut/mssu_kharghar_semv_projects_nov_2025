package com.expense.repository;

import com.expense.model.Category;
import com.expense.model.Expense;
import com.expense.model.ExpenseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Expense entity.
 * Provides database access methods for expense management with custom query methods for filtering.
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    /**
     * Find all expenses for a specific user with pagination support.
     * Used for displaying user's expense list.
     *
     * @param userId the ID of the user
     * @param pageable pagination and sorting parameters
     * @return Page of expenses belonging to the user
     */
    Page<Expense> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Find all expenses for a specific user filtered by status.
     * Used for filtering expenses by their approval status.
     *
     * @param userId the ID of the user
     * @param status the expense status to filter by
     * @return List of expenses matching the criteria
     */
    List<Expense> findByUserIdAndStatus(Long userId, ExpenseStatus status);
    
    /**
     * Find all expenses with a specific status.
     * Used by managers to retrieve pending expenses for approval.
     *
     * @param status the expense status to filter by
     * @return List of expenses with the specified status
     */
    List<Expense> findByStatus(ExpenseStatus status);
    
    /**
     * Find all expenses for a user within a date range.
     * Used for generating reports filtered by date.
     *
     * @param userId the ID of the user
     * @param startDate the start date of the range (inclusive)
     * @param endDate the end date of the range (inclusive)
     * @return List of expenses within the date range
     */
    List<Expense> findByUserIdAndExpenseDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find all expenses for a user filtered by category.
     * Used for generating reports filtered by category.
     *
     * @param userId the ID of the user
     * @param category the category to filter by
     * @return List of expenses in the specified category
     */
    List<Expense> findByUserIdAndCategory(Long userId, Category category);
}
