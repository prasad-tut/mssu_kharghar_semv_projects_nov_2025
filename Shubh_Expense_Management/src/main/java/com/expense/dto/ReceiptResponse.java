package com.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for receipt information in responses.
 * Contains receipt metadata without the actual file content.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptResponse {
    
    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
}
