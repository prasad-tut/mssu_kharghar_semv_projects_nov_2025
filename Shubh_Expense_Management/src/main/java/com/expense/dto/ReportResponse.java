package com.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO for expense report responses.
 * Contains filtered expenses, aggregated totals, and filter criteria.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    
    private List<ExpenseResponse> expenses;
    private BigDecimal totalAmount;
    private Integer count;
    private ReportFilters filters;
    
    /**
     * Inner class to represent the filters applied to the report.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportFilters {
        private LocalDate startDate;
        private LocalDate endDate;
        private Long categoryId;
        private String status;
    }
}
