package com.expense.controller;

import com.expense.dto.ReportResponse;
import com.expense.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

/**
 * REST controller for expense reporting endpoints.
 * Provides endpoints for generating reports with filters and exporting data.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {
    
    private final ReportService reportService;
    
    /**
     * Generate expense report summary with optional filters.
     * Filters can include date range, category, and status.
     *
     * @param authentication the authenticated user
     * @param startDate optional start date for filtering (format: yyyy-MM-dd)
     * @param endDate optional end date for filtering (format: yyyy-MM-dd)
     * @param categoryId optional category ID for filtering
     * @param status optional status for filtering (DRAFT, SUBMITTED, APPROVED, REJECTED)
     * @return ResponseEntity containing ReportResponse with filtered expenses and totals
     */
    @GetMapping("/summary")
    public ResponseEntity<ReportResponse> getReportSummary(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status) {
        
        String userEmail = authentication.getName();
        log.info("GET /api/reports/summary - User: {}, Filters: startDate={}, endDate={}, categoryId={}, status={}",
                userEmail, startDate, endDate, categoryId, status);
        
        ReportResponse report = reportService.generateReport(userEmail, startDate, endDate, categoryId, status);
        
        log.info("Report summary generated successfully for user: {}", userEmail);
        return ResponseEntity.ok(report);
    }
    
    /**
     * Export expense report in specified format.
     * Currently supports CSV format.
     *
     * @param authentication the authenticated user
     * @param format the export format (csv)
     * @param startDate optional start date for filtering (format: yyyy-MM-dd)
     * @param endDate optional end date for filtering (format: yyyy-MM-dd)
     * @param categoryId optional category ID for filtering
     * @param status optional status for filtering (DRAFT, SUBMITTED, APPROVED, REJECTED)
     * @return ResponseEntity containing the exported file as byte array
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportReport(
            Authentication authentication,
            @RequestParam(defaultValue = "csv") String format,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status) {
        
        String userEmail = authentication.getName();
        log.info("GET /api/reports/export - User: {}, Format: {}, Filters: startDate={}, endDate={}, categoryId={}, status={}",
                userEmail, format, startDate, endDate, categoryId, status);
        
        try {
            byte[] exportData;
            String filename;
            MediaType mediaType;
            
            // Handle different export formats
            if ("csv".equalsIgnoreCase(format)) {
                exportData = reportService.exportReportAsCsv(userEmail, startDate, endDate, categoryId, status);
                filename = "expense_report_" + LocalDate.now() + ".csv";
                mediaType = MediaType.parseMediaType("text/csv");
            } else {
                log.warn("Unsupported export format requested: {}", format);
                return ResponseEntity.badRequest()
                        .body(("Unsupported format: " + format + ". Supported formats: csv").getBytes());
            }
            
            // Set response headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(exportData.length);
            
            log.info("Report exported successfully for user: {} in format: {}", userEmail, format);
            return new ResponseEntity<>(exportData, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            log.error("Error exporting report for user: {}", userEmail, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating export: " + e.getMessage()).getBytes());
        }
    }
}
