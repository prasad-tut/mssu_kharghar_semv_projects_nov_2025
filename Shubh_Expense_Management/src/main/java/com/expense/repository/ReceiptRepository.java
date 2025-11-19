package com.expense.repository;

import com.expense.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Receipt entity.
 * Provides database access methods for receipt file management.
 */
@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    
    /**
     * Find a receipt by its associated expense ID.
     * Used for retrieving receipt information for a specific expense.
     *
     * @param expenseId the ID of the expense
     * @return Optional containing the receipt if found, empty otherwise
     */
    Optional<Receipt> findByExpenseId(Long expenseId);
}
