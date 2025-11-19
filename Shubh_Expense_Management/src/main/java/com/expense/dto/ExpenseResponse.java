package com.expense.dto;

import com.expense.model.ExpenseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for expense information in responses.
 * Contains complete expense details including related entities.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {
    
    private Long id;
    private CategoryResponse category;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String description;
    private ExpenseStatus status;
    private ReceiptResponse receipt;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private UserResponse reviewedBy;
    private String reviewNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
