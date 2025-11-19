package com.expense.service;

import com.expense.dto.ReportResponse;
import com.expense.exception.ResourceNotFoundException;
import com.expense.model.*;
import com.expense.repository.CategoryRepository;
import com.expense.repository.ExpenseRepository;
import com.expense.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private ReportService reportService;

    private User testUser;
    private Category testCategory;
    private Expense expense1;
    private Expense expense2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setRole(UserRole.USER);

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Travel");
        testCategory.setDescription("Travel expenses");

        expense1 = new Expense();
        expense1.setId(1L);
        expense1.setUser(testUser);
        expense1.setCategory(testCategory);
        expense1.setAmount(new BigDecimal("100.00"));
        expense1.setExpenseDate(LocalDate.of(2024, 1, 15));
        expense1.setDescription("Expense 1");
        expense1.setStatus(ExpenseStatus.APPROVED);

        expense2 = new Expense();
        expense2.setId(2L);
        expense2.setUser(testUser);
        expense2.setCategory(testCategory);
        expense2.setAmount(new BigDecimal("200.00"));
        expense2.setExpenseDate(LocalDate.of(2024, 1, 20));
        expense2.setDescription("Expense 2");
        expense2.setStatus(ExpenseStatus.SUBMITTED);
    }

    @Test
    void generateReport_NoFilters_Success() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(expenseRepository.findByUserId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(expense1, expense2)));

        // Act
        ReportResponse response = reportService.generateReport("user@example.com", null, null, null, null);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getCount());
        assertEquals(new BigDecimal("300.00"), response.getTotalAmount());
        assertEquals(2, response.getExpenses().size());

        verify(userRepository).findByEmail("user@example.com");
        verify(expenseRepository).findByUserId(1L, Pageable.unpaged());
    }

    @Test
    void generateReport_WithDateRangeFilter_Success() {
        // Arrange
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 18);
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(expenseRepository.findByUserId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(expense1, expense2)));

        // Act
        ReportResponse response = reportService.generateReport("user@example.com", startDate, endDate, null, null);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getCount());
        assertEquals(new BigDecimal("100.00"), response.getTotalAmount());
        assertEquals(1, response.getExpenses().size());
        assertEquals(expense1.getId(), response.getExpenses().get(0).getId());

        verify(userRepository).findByEmail("user@example.com");
    }

    @Test
    void generateReport_WithCategoryFilter_Success() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(expenseRepository.findByUserId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(expense1, expense2)));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));

        // Act
        ReportResponse response = reportService.generateReport("user@example.com", null, null, 1L, null);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getCount());
        assertEquals(new BigDecimal("300.00"), response.getTotalAmount());

        verify(categoryRepository).findById(1L);
    }

    @Test
    void generateReport_WithStatusFilter_Success() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(expenseRepository.findByUserId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(expense1, expense2)));

        // Act
        ReportResponse response = reportService.generateReport("user@example.com", null, null, null, "APPROVED");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getCount());
        assertEquals(new BigDecimal("100.00"), response.getTotalAmount());
        assertEquals(1, response.getExpenses().size());
        assertEquals(ExpenseStatus.APPROVED, response.getExpenses().get(0).getStatus());
    }

    @Test
    void generateReport_InvalidStatus_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(expenseRepository.findByUserId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(expense1, expense2)));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reportService.generateReport("user@example.com", null, null, null, "INVALID"));

        assertTrue(exception.getMessage().contains("Invalid expense status"));
    }

    @Test
    void generateReport_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> reportService.generateReport("nonexistent@example.com", null, null, null, null));

        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void generateReport_CategoryNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(expenseRepository.findByUserId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(expense1, expense2)));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> reportService.generateReport("user@example.com", null, null, 999L, null));

        verify(categoryRepository).findById(999L);
    }

    @Test
    void exportReportAsCsv_Success() throws IOException {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(expenseRepository.findByUserId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(expense1)));

        // Act
        byte[] csvData = reportService.exportReportAsCsv("user@example.com", null, null, null, null);

        // Assert
        assertNotNull(csvData);
        assertTrue(csvData.length > 0);
        
        String csvContent = new String(csvData);
        assertTrue(csvContent.contains("ID,Date,Category,Amount,Description,Status"));
        assertTrue(csvContent.contains("Travel"));
        assertTrue(csvContent.contains("100.00"));
        assertTrue(csvContent.contains("Total Amount"));

        verify(userRepository).findByEmail("user@example.com");
    }

    @Test
    void generateReport_MultipleFilters_Success() {
        // Arrange
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(expenseRepository.findByUserId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(expense1, expense2)));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));

        // Act
        ReportResponse response = reportService.generateReport(
                "user@example.com", startDate, endDate, 1L, "APPROVED");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getCount());
        assertEquals(new BigDecimal("100.00"), response.getTotalAmount());
        assertEquals(ExpenseStatus.APPROVED, response.getExpenses().get(0).getStatus());
    }
}
