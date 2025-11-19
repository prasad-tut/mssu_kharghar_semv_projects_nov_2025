package com.expense.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a receipt file attached to an expense.
 * Stores metadata about uploaded receipt documents.
 */
@Entity
@Table(name = "receipts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false, unique = true)
    private Expense expense;
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false, length = 500)
    private String filePath;
    
    @Column(nullable = false, length = 50)
    private String fileType;
    
    @Column(nullable = false)
    private Long fileSize;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;
    
    /**
     * Automatically set uploadedAt timestamp before persisting
     */
    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}
