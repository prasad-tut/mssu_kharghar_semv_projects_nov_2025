package com.expense.service;

import com.expense.dto.ExpenseResponse;
import com.expense.dto.ReportResponse;
import com.expense.exception.ResourceNotFoundException;
import com.expense.model.Category;
import com.expense.model.Expense;
import com.expense.model.ExpenseStatus;
import com.expense.model.User;
import com.expense.repository.CategoryRepository;
import com.expense.repository.ExpenseRepository;
import com.expense.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for generating expense reports.
 * Handles filtering, aggregation, and export functionality.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseService expenseService;
    
    /**
     * Generate an expense report with optional filters.
     * Filters expenses by date range, category, and status, then calculates totals.
     *
     * @param userEmail the email of the authenticated user
     * @param startDate optional start date for filtering
     * @param endDate optional end date for filtering
     * @param categoryId optional category ID for filtering
     * @param status optional status for filtering
     * @return ReportResponse containing filtered expenses and aggregated data
     * @throws ResourceNotFoundException if user or category not found
     */
    @Transactional(readOnly = true)
    public ReportResponse generateReport(String userEmail, LocalDate startDate, LocalDate endDate, 
                                         Long categoryId, String status) {
        log.info("Generating report for user: {} with filters - startDate: {}, endDate: {}, categoryId: {}, status: {}",
                userEmail, startDate, endDate, categoryId, status);
        
        // Fetch user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        // Start with all user expenses
        List<Expense> expenses = expenseRepository.findByUserId(user.getId(), 
                org.springframework.data.domain.Pageable.unpaged()).getContent();
        
        // Apply date range filter
        if (startDate != null && endDate != null) {
            expenses = filterByDateRange(expenses, startDate, endDate);
            log.debug("After date filter: {} expenses", expenses.size());
        }
        
        // Apply category filter
        if (categoryId != null) {
            expenses = filterByCategory(expenses, categoryId);
            log.debug("After category filter: {} expenses", expenses.size());
        }
        
        // Apply status filter
        if (status != null && !status.isEmpty()) {
            expenses = filterByStatus(expenses, status);
            log.debug("After status filter: {} expenses", expenses.size());
        }
        
        // Calculate total amount
        BigDecimal totalAmount = calculateTotalAmount(expenses);
        
        // Map to response DTOs
        List<ExpenseResponse> expenseResponses = expenses.stream()
                .map(this::mapToExpenseResponse)
                .collect(Collectors.toList());
        
        // Create filter object
        ReportResponse.ReportFilters filters = new ReportResponse.ReportFilters(
                startDate, endDate, categoryId, status
        );
        
        log.info("Report generated successfully: {} expenses, total amount: {}", 
                expenseResponses.size(), totalAmount);
        
        return new ReportResponse(expenseResponses, totalAmount, expenseResponses.size(), filters);
    }
    
    /**
     * Filter expenses by date range.
     *
     * @param expenses list of expenses to filter
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @return filtered list of expenses
     */
    private List<Expense> filterByDateRange(List<Expense> expenses, LocalDate startDate, LocalDate endDate) {
        return expenses.stream()
                .filter(expense -> !expense.getExpenseDate().isBefore(startDate) && 
                                 !expense.getExpenseDate().isAfter(endDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Filter expenses by category.
     *
     * @param expenses list of expenses to filter
     * @param categoryId category ID to filter by
     * @return filtered list of expenses
     * @throws ResourceNotFoundException if category not found
     */
    private List<Expense> filterByCategory(List<Expense> expenses, Long categoryId) {
        // Validate category exists
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        
        return expenses.stream()
                .filter(expense -> expense.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());
    }
    
    /**
     * Filter expenses by status.
     *
     * @param expenses list of expenses to filter
     * @param status status string to filter by
     * @return filtered list of expenses
     * @throws IllegalArgumentException if status is invalid
     */
    private List<Expense> filterByStatus(List<Expense> expenses, String status) {
        try {
            ExpenseStatus expenseStatus = ExpenseStatus.valueOf(status.toUpperCase());
            return expenses.stream()
                    .filter(expense -> expense.getStatus() == expenseStatus)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            log.error("Invalid expense status: {}", status);
            throw new IllegalArgumentException("Invalid expense status: " + status);
        }
    }
    
    /**
     * Calculate total amount for a list of expenses.
     *
     * @param expenses list of expenses
     * @return total amount as BigDecimal
     */
    private BigDecimal calculateTotalAmount(List<Expense> expenses) {
        return expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Export report as CSV format.
     * Generates a CSV file with expense details.
     *
     * @param userEmail the email of the authenticated user
     * @param startDate optional start date for filtering
     * @param endDate optional end date for filtering
     * @param categoryId optional category ID for filtering
     * @param status optional status for filtering
     * @return byte array containing CSV data
     * @throws IOException if CSV generation fails
     */
    public byte[] exportReportAsCsv(String userEmail, LocalDate startDate, LocalDate endDate,
                                    Long categoryId, String status) throws IOException {
        log.info("Exporting report as CSV for user: {}", userEmail);
        
        // Generate report with filters
        ReportResponse report = generateReport(userEmail, startDate, endDate, categoryId, status);
        
        // Create CSV content
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        
        // Write CSV header
        writer.println("ID,Date,Category,Amount,Description,Status,Submitted At,Reviewed At");
        
        // Write expense data
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        
        for (ExpenseResponse expense : report.getExpenses()) {
            writer.printf("%d,%s,%s,%.2f,\"%s\",%s,%s,%s%n",
                    expense.getId(),
                    expense.getExpenseDate().format(dateFormatter),
                    expense.getCategory().getName(),
                    expense.getAmount(),
                    escapeCsvField(expense.getDescription()),
                    expense.getStatus(),
                    expense.getSubmittedAt() != null ? expense.getSubmittedAt().format(dateTimeFormatter) : "",
                    expense.getReviewedAt() != null ? expense.getReviewedAt().format(dateTimeFormatter) : ""
            );
        }
        
        // Write summary
        writer.println();
        writer.printf("Total Expenses,%d%n", report.getCount());
        writer.printf("Total Amount,%.2f%n", report.getTotalAmount());
        
        writer.flush();
        writer.close();
        
        log.info("CSV export completed successfully: {} bytes", outputStream.size());
        return outputStream.toByteArray();
    }
    
    /**
     * Escape special characters in CSV fields.
     *
     * @param field the field to escape
     * @return escaped field
     */
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        // Replace double quotes with two double quotes for CSV escaping
        return field.replace("\"", "\"\"");
    }
    
    /**
     * Map Expense entity to ExpenseResponse DTO.
     * Reuses the mapping logic from ExpenseService.
     *
     * @param expense the Expense entity
     * @return ExpenseResponse DTO
     */
    private ExpenseResponse mapToExpenseResponse(Expense expense) {
        com.expense.dto.CategoryResponse categoryResponse = new com.expense.dto.CategoryResponse(
                expense.getCategory().getId(),
                expense.getCategory().getName(),
                expense.getCategory().getDescription()
        );
        
        com.expense.dto.ReceiptResponse receiptResponse = null;
        if (expense.getReceipt() != null) {
            receiptResponse = new com.expense.dto.ReceiptResponse(
                    expense.getReceipt().getId(),
                    expense.getReceipt().getFileName(),
                    expense.getReceipt().getFileType(),
                    expense.getReceipt().getFileSize(),
                    expense.getReceipt().getUploadedAt()
            );
        }
        
        com.expense.dto.UserResponse reviewedByResponse = null;
        if (expense.getReviewedBy() != null) {
            reviewedByResponse = new com.expense.dto.UserResponse(
                    expense.getReviewedBy().getId(),
                    expense.getReviewedBy().getEmail(),
                    expense.getReviewedBy().getFirstName(),
                    expense.getReviewedBy().getLastName(),
                    expense.getReviewedBy().getRole()
            );
        }
        
        return new com.expense.dto.ExpenseResponse(
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
