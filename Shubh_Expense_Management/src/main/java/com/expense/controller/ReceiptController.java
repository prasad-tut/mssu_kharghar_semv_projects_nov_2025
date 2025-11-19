package com.expense.controller;

import com.expense.dto.ReceiptResponse;
import com.expense.service.ReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for receipt management endpoints.
 * Handles receipt upload, download, and deletion operations.
 */
@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Receipt Management", description = "APIs for managing expense receipts")
@SecurityRequirement(name = "Bearer Authentication")
public class ReceiptController {
    
    private final ReceiptService receiptService;
    
    /**
     * Upload a receipt file for an expense.
     *
     * @param expenseId the ID of the expense
     * @param file the receipt file to upload
     * @param authentication the authenticated user
     * @return ResponseEntity with ReceiptResponse
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload receipt",
            description = "Upload a receipt file (JPEG, PNG, or PDF) for an expense. Maximum file size is 5 MB."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Receipt uploaded successfully",
                    content = @Content(schema = @Schema(implementation = ReceiptResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid file or file validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User doesn't own the expense"),
            @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    public ResponseEntity<ReceiptResponse> uploadReceipt(
            @Parameter(description = "ID of the expense", required = true)
            @RequestParam Long expenseId,
            
            @Parameter(description = "Receipt file (JPEG, PNG, or PDF, max 5 MB)", required = true)
            @RequestParam("file") MultipartFile file,
            
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        log.info("Upload receipt request for expense ID: {} by user: {}", expenseId, userEmail);
        
        ReceiptResponse response = receiptService.uploadReceipt(expenseId, file, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Download a receipt file by receipt ID.
     *
     * @param id the ID of the receipt
     * @param authentication the authenticated user
     * @return ResponseEntity with the file resource
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Download receipt",
            description = "Download a receipt file by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receipt file retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User doesn't own the expense"),
            @ApiResponse(responseCode = "404", description = "Receipt not found")
    })
    public ResponseEntity<Resource> downloadReceipt(
            @Parameter(description = "ID of the receipt", required = true)
            @PathVariable Long id,
            
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        log.info("Download receipt request for receipt ID: {} by user: {}", id, userEmail);
        
        Resource resource = receiptService.getReceiptFile(id, userEmail);
        ReceiptResponse metadata = receiptService.getReceiptMetadata(id, userEmail);
        
        // Determine content type
        String contentType = metadata.getFileType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + metadata.getFileName() + "\"")
                .body(resource);
    }
    
    /**
     * Delete a receipt by ID.
     *
     * @param id the ID of the receipt to delete
     * @param authentication the authenticated user
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete receipt",
            description = "Delete a receipt file and its metadata"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Receipt deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User doesn't own the expense"),
            @ApiResponse(responseCode = "404", description = "Receipt not found")
    })
    public ResponseEntity<Void> deleteReceipt(
            @Parameter(description = "ID of the receipt", required = true)
            @PathVariable Long id,
            
            Authentication authentication) {
        
        String userEmail = authentication.getName();
        log.info("Delete receipt request for receipt ID: {} by user: {}", id, userEmail);
        
        receiptService.deleteReceipt(id, userEmail);
        return ResponseEntity.noContent().build();
    }
}
