package com.expense.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for expense approval or rejection requests.
 * Contains the approval decision and optional review notes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequest {
    
    @NotNull(message = "Approval decision is required")
    private Boolean approved;
    
    @Size(max = 500, message = "Review notes must not exceed 500 characters")
    private String reviewNotes;
}
