package com.expense.service;

import com.expense.dto.ExpenseRequest;
import com.expense.dto.ExpenseResponse;
import com.expense.exception.ResourceNotFoundException;
import com.expense.exception.UnauthorizedException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private User testUser;
    private User managerUser;
    private Category testCategory;
    private Expense testExpense;
    private ExpenseRequest expenseRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setRole(UserRole.USER);

        managerUser = new User();
        managerUser.setId(2L);
        managerUser.setEmail("manager@example.com");
        managerUser.setFirstName("Jane");
        managerUser.setLastName("Manager");
        managerUser.setRole(UserRole.MANAGER);

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Travel");
        testCategory.setDescription("Travel expenses");

        testExpense = new Expense();
        testExpense.setId(1L);
        testExpense.setUser(testUser);
        testExpense.setCategory(testCategory);
        testExpense.setAmount(new BigDecimal("100.00"));
        testExpense.setExpenseDate(LocalDate.now());
        testExpense.setDescription("Test expense");
        testExpense.setStatus(ExpenseStatus.DRAFT);

        expenseRequest = new ExpenseRequest();
        expenseRequest.setCategoryId(1L);
        expenseRequest.setAmount(new BigDecimal("100.00"));
        expenseRequest.setExpenseDate(LocalDate.now());
        expenseRequest.setDescription("Test expense");
    }

    @Test
    void createExpense_Success() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        // Act
        ExpenseResponse response = expenseService.createExpense(expenseRequest, "user@example.com");

        // Assert
        assertNotNull(response);
        assertEquals(testExpense.getId(), response.getId());
        assertEquals(testExpense.getAmount(), response.getAmount());
        assertEquals(ExpenseStatus.DRAFT, response.getStatus());

        verify(userRepository).findByEmail("user@example.com");
        verify(categoryRepository).findById(1L);
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    void createExpense_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> expenseService.createExpense(expenseRequest, "nonexistent@example.com"));

        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void createExpense_CategoryNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> expenseService.createExpense(expenseRequest, "user@example.com"));

        verify(categoryRepository).findById(1L);
        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void getAllExpensesForUser_Success() {
        // Arrange
        Page<Expense> expensePage = new PageImpl<>(List.of(testExpense));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(expenseRepository.findByUserId(anyLong(), any(Pageable.class))).thenReturn(expensePage);

        // Act
        Page<ExpenseResponse> response = expenseService.getAllExpensesForUser("user@example.com", Pageable.unpaged());

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(testExpense.getId(), response.getContent().get(0).getId());

        verify(userRepository).findByEmail("user@example.com");
        verify(expenseRepository).findByUserId(1L, Pageable.unpaged());
    }

    @Test
    void getExpenseById_Success() {
        // Arrange
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.of(testExpense));

        // Act
        ExpenseResponse response = expenseService.getExpenseById(1L, "user@example.com");

        // Assert
        assertNotNull(response);
        assertEquals(testExpense.getId(), response.getId());
        assertEquals(testExpense.getAmount(), response.getAmount());

        verify(expenseRepository).findById(1L);
    }

    @Test
    void getExpenseById_UnauthorizedAccess_ThrowsException() {
        // Arrange
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.of(testExpense));

        // Act & Assert
        assertThrows(UnauthorizedException.class,
                () -> expenseService.getExpenseById(1L, "other@example.com"));

        verify(expenseRepository).findById(1L);
    }

    @Test
    void updateExpense_Success() {
        // Arrange
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.of(testExpense));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        // Act
        ExpenseResponse response = expenseService.updateExpense(1L, expenseRequest, "user@example.com");

        // Assert
        assertNotNull(response);
        verify(expenseRepository).findById(1L);
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    void updateExpense_NonDraftStatus_ThrowsException() {
        // Arrange
        testExpense.setStatus(ExpenseStatus.SUBMITTED);
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.of(testExpense));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> expenseService.updateExpense(1L, expenseRequest, "user@example.com"));

        assertEquals("Only expenses in DRAFT status can be updated", exception.getMessage());
        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void deleteExpense_Success() {
        // Arrange
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.of(testExpense));

        // Act
        expenseService.deleteExpense(1L, "user@example.com");

        // Assert
        verify(expenseRepository).findById(1L);
        verify(expenseRepository).delete(testExpense);
    }

    @Test
    void deleteExpense_NonDraftStatus_ThrowsException() {
        // Arrange
        testExpense.setStatus(ExpenseStatus.APPROVED);
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.of(testExpense));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> expenseService.deleteExpense(1L, "user@example.com"));

        assertEquals("Only expenses in DRAFT status can be deleted", exception.getMessage());
        verify(expenseRepository, never()).delete(any(Expense.class));
    }

    @Test
    void submitExpenseForApproval_Success() {
        // Arrange
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.of(testExpense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        // Act
        ExpenseResponse response = expenseService.submitExpenseForApproval(1L, "user@example.com");

        // Assert
        assertNotNull(response);
        verify(expenseRepository).findById(1L);
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    void getPendingExpenses_ManagerAccess_Success() {
        // Arrange
        testExpense.setStatus(ExpenseStatus.SUBMITTED);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(managerUser));
        when(expenseRepository.findByStatus(ExpenseStatus.SUBMITTED)).thenReturn(List.of(testExpense));

        // Act
        List<ExpenseResponse> response = expenseService.getPendingExpenses("manager@example.com");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        verify(userRepository).findByEmail("manager@example.com");
        verify(expenseRepository).findByStatus(ExpenseStatus.SUBMITTED);
    }

    @Test
    void getPendingExpenses_NonManagerAccess_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(UnauthorizedException.class,
                () -> expenseService.getPendingExpenses("user@example.com"));

        verify(userRepository).findByEmail("user@example.com");
        verify(expenseRepository, never()).findByStatus(any());
    }

    @Test
    void approveExpense_Success() {
        // Arrange
        testExpense.setStatus(ExpenseStatus.SUBMITTED);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(managerUser));
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.of(testExpense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        // Act
        ExpenseResponse response = expenseService.approveExpense(1L, "Approved", "manager@example.com");

        // Assert
        assertNotNull(response);
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    void approveExpense_NonManagerAccess_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(UnauthorizedException.class,
                () -> expenseService.approveExpense(1L, "Approved", "user@example.com"));

        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void rejectExpense_Success() {
        // Arrange
        testExpense.setStatus(ExpenseStatus.SUBMITTED);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(managerUser));
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.of(testExpense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        // Act
        ExpenseResponse response = expenseService.rejectExpense(1L, "Rejected", "manager@example.com");

        // Assert
        assertNotNull(response);
        verify(expenseRepository).save(any(Expense.class));
    }
}
