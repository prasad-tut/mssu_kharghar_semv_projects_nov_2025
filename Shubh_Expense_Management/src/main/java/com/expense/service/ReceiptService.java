package com.expense.service;

import com.expense.dto.ReceiptResponse;
import com.expense.exception.FileUploadException;
import com.expense.exception.ResourceNotFoundException;
import com.expense.exception.UnauthorizedException;
import com.expense.model.Expense;
import com.expense.model.Receipt;
import com.expense.model.User;
import com.expense.repository.ExpenseRepository;
import com.expense.repository.ReceiptRepository;
import com.expense.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing receipt file uploads and storage.
 * Handles file validation, storage, retrieval, and deletion.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptService {
    
    private final ReceiptRepository receiptRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "application/pdf"
    );
    
    /**
     * Upload a receipt file for an expense.
     * Validates file type and size, stores the file, and creates a receipt record.
     *
     * @param expenseId the ID of the expense
     * @param file the multipart file to upload
     * @param userEmail the email of the authenticated user
     * @return ReceiptResponse containing receipt metadata
     * @throws ResourceNotFoundException if expense not found
     * @throws UnauthorizedException if user doesn't own the expense
     * @throws FileUploadException if file validation or storage fails
     */
    @Transactional
    public ReceiptResponse uploadReceipt(Long expenseId, MultipartFile file, String userEmail) {
        log.info("Uploading receipt for expense ID: {} by user: {}", expenseId, userEmail);
        
        // Fetch user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        // Validate file
        validateFile(file);
        
        // Find expense and verify ownership
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
        
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to upload receipt for this expense");
        }
        
        // Delete existing receipt if present
        if (expense.getReceipt() != null) {
            deleteReceiptFile(expense.getReceipt());
            receiptRepository.delete(expense.getReceipt());
        }
        
        // Store file
        String storedFileName = storeFile(file);
        
        // Create receipt entity
        Receipt receipt = new Receipt();
        receipt.setExpense(expense);
        receipt.setFileName(file.getOriginalFilename());
        receipt.setFilePath(storedFileName);
        receipt.setFileType(file.getContentType());
        receipt.setFileSize(file.getSize());
        
        Receipt savedReceipt = receiptRepository.save(receipt);
        log.info("Receipt uploaded successfully with ID: {}", savedReceipt.getId());
        
        return mapToReceiptResponse(savedReceipt);
    }
    
    /**
     * Retrieve a receipt file by receipt ID.
     * Verifies user authorization before returning the file.
     *
     * @param receiptId the ID of the receipt
     * @param userEmail the email of the authenticated user
     * @return Resource containing the file
     * @throws ResourceNotFoundException if receipt not found or file doesn't exist
     * @throws UnauthorizedException if user doesn't own the expense
     */
    @Transactional(readOnly = true)
    public Resource getReceiptFile(Long receiptId, String userEmail) {
        log.info("Retrieving receipt file ID: {} for user: {}", receiptId, userEmail);
        
        // Fetch user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt", "id", receiptId));
        
        // Verify ownership
        if (!receipt.getExpense().getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to access this receipt");
        }
        
        try {
            Path filePath = Paths.get(uploadDir).resolve(receipt.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found: " + receipt.getFileName());
            }
        } catch (IOException ex) {
            throw new FileUploadException("Error reading file: " + receipt.getFileName(), ex);
        }
    }
    
    /**
     * Get receipt metadata by receipt ID.
     *
     * @param receiptId the ID of the receipt
     * @param userEmail the email of the authenticated user
     * @return ReceiptResponse containing receipt metadata
     * @throws ResourceNotFoundException if receipt not found
     * @throws UnauthorizedException if user doesn't own the expense
     */
    @Transactional(readOnly = true)
    public ReceiptResponse getReceiptMetadata(Long receiptId, String userEmail) {
        log.info("Retrieving receipt metadata ID: {} for user: {}", receiptId, userEmail);
        
        // Fetch user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt", "id", receiptId));
        
        // Verify ownership
        if (!receipt.getExpense().getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to access this receipt");
        }
        
        return mapToReceiptResponse(receipt);
    }
    
    /**
     * Delete a receipt by ID.
     * Removes both the database record and the physical file.
     *
     * @param receiptId the ID of the receipt to delete
     * @param userEmail the email of the authenticated user
     * @throws ResourceNotFoundException if receipt not found
     * @throws UnauthorizedException if user doesn't own the expense
     */
    @Transactional
    public void deleteReceipt(Long receiptId, String userEmail) {
        log.info("Deleting receipt ID: {} by user: {}", receiptId, userEmail);
        
        // Fetch user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        
        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt", "id", receiptId));
        
        // Verify ownership
        if (!receipt.getExpense().getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this receipt");
        }
        
        // Delete physical file
        deleteReceiptFile(receipt);
        
        // Delete database record
        receiptRepository.delete(receipt);
        log.info("Receipt deleted successfully with ID: {}", receiptId);
    }
    
    /**
     * Validate uploaded file for type and size.
     *
     * @param file the file to validate
     * @throws FileUploadException if validation fails
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("File is empty or null");
        }
        
        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileUploadException("File size exceeds maximum limit of 5 MB");
        }
        
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_FILE_TYPES.contains(contentType.toLowerCase())) {
            throw new FileUploadException("Invalid file type. Allowed types: JPEG, PNG, PDF");
        }
    }
    
    /**
     * Store file to the configured upload directory.
     *
     * @param file the file to store
     * @return the stored file name
     * @throws FileUploadException if storage fails
     */
    private String storeFile(MultipartFile file) {
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String storedFileName = UUID.randomUUID().toString() + fileExtension;
            
            // Copy file to upload directory
            Path targetLocation = uploadPath.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("File stored successfully: {}", storedFileName);
            return storedFileName;
            
        } catch (IOException ex) {
            throw new FileUploadException("Failed to store file", ex);
        }
    }
    
    /**
     * Delete physical receipt file from storage.
     *
     * @param receipt the receipt containing file path
     */
    private void deleteReceiptFile(Receipt receipt) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(receipt.getFilePath()).normalize();
            Files.deleteIfExists(filePath);
            log.info("File deleted successfully: {}", receipt.getFilePath());
        } catch (IOException ex) {
            log.error("Failed to delete file: {}", receipt.getFilePath(), ex);
            // Don't throw exception, continue with database deletion
        }
    }
    
    /**
     * Map Receipt entity to ReceiptResponse DTO.
     *
     * @param receipt the receipt entity
     * @return ReceiptResponse DTO
     */
    private ReceiptResponse mapToReceiptResponse(Receipt receipt) {
        return new ReceiptResponse(
                receipt.getId(),
                receipt.getFileName(),
                receipt.getFileType(),
                receipt.getFileSize(),
                receipt.getUploadedAt()
        );
    }
}
