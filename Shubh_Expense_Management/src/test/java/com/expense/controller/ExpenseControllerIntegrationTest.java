package com.expense.controller;

import com.expense.dto.ApprovalRequest;
import com.expense.dto.ExpenseRequest;
import com.expense.model.*;
import com.expense.repository.CategoryRepository;
import com.expense.repository.ExpenseRepository;
import com.expense.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ExpenseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private User managerUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        expenseRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setEmail("user@example.com");
        testUser.setPasswordHash(passwordEncoder.encode("password"));
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setRole(UserRole.USER);
        testUser = userRepository.save(testUser);

        // Create manager user
        managerUser = new User();
        managerUser.setEmail("manager@example.com");
        managerUser.setPasswordHash(passwordEncoder.encode("password"));
        managerUser.setFirstName("Jane");
        managerUser.setLastName("Manager");
        managerUser.setRole(UserRole.MANAGER);
        managerUser = userRepository.save(managerUser);

        // Create test category
        testCategory = new Category();
        testCategory.setName("Travel");
        testCategory.setDescription("Travel expenses");
        testCategory = categoryRepository.save(testCategory);
    }

    @Test
    void createExpense_Success() throws Exception {
        // Arrange
        ExpenseRequest request = new ExpenseRequest();
        request.setCategoryId(testCategory.getId());
        request.setAmount(new BigDecimal("100.00"));
        request.setExpenseDate(LocalDate.now());
        request.setDescription("Test expense");

        // Act & Assert
        mockMvc.perform(post("/api/expenses")
                        .with(user(testUser.getEmail()).roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.description").value("Test expense"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.category.name").value("Travel"));
    }

    @Test
    void createExpense_Unauthorized_ReturnsUnauthorized() throws Exception {
        // Arrange
        ExpenseRequest request = new ExpenseRequest();
        request.setCategoryId(testCategory.getId());
        request.setAmount(new BigDecimal("100.00"));
        request.setExpenseDate(LocalDate.now());
        request.setDescription("Test expense");

        // Act & Assert
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllExpenses_Success() throws Exception {
        // Arrange - Create expenses
        Expense expense1 = createExpense(testUser, testCategory, new BigDecimal("100.00"));
        Expense expense2 = createExpense(testUser, testCategory, new BigDecimal("200.00"));

        // Act & Assert
        mockMvc.perform(get("/api/expenses")
                        .with(user(testUser.getEmail()).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getExpenseById_Success() throws Exception {
        // Arrange
        Expense expense = createExpense(testUser, testCategory, new BigDecimal("100.00"));

        // Act & Assert
        mockMvc.perform(get("/api/expenses/" + expense.getId())
                        .with(user(testUser.getEmail()).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expense.getId()))
                .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    void getExpenseById_UnauthorizedAccess_ReturnsForbidden() throws Exception {
        // Arrange
        Expense expense = createExpense(testUser, testCategory, new BigDecimal("100.00"));

        // Act & Assert - Different user trying to access
        mockMvc.perform(get("/api/expenses/" + expense.getId())
                        .with(user("other@example.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateExpense_Success() throws Exception {
        // Arrange
        Expense expense = createExpense(testUser, testCategory, new BigDecimal("100.00"));

        ExpenseRequest updateRequest = new ExpenseRequest();
        updateRequest.setCategoryId(testCategory.getId());
        updateRequest.setAmount(new BigDecimal("150.00"));
        updateRequest.setExpenseDate(LocalDate.now());
        updateRequest.setDescription("Updated expense");

        // Act & Assert
        mockMvc.perform(put("/api/expenses/" + expense.getId())
                        .with(user(testUser.getEmail()).roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(150.00))
                .andExpect(jsonPath("$.description").value("Updated expense"));
    }

    @Test
    void updateExpense_NonDraftStatus_ReturnsBadRequest() throws Exception {
        // Arrange
        Expense expense = createExpense(testUser, testCategory, new BigDecimal("100.00"));
        expense.setStatus(ExpenseStatus.SUBMITTED);
        expenseRepository.save(expense);

        ExpenseRequest updateRequest = new ExpenseRequest();
        updateRequest.setCategoryId(testCategory.getId());
        updateRequest.setAmount(new BigDecimal("150.00"));
        updateRequest.setExpenseDate(LocalDate.now());
        updateRequest.setDescription("Updated expense");

        // Act & Assert
        mockMvc.perform(put("/api/expenses/" + expense.getId())
                        .with(user(testUser.getEmail()).roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteExpense_Success() throws Exception {
        // Arrange
        Expense expense = createExpense(testUser, testCategory, new BigDecimal("100.00"));

        // Act & Assert
        mockMvc.perform(delete("/api/expenses/" + expense.getId())
                        .with(user(testUser.getEmail()).roles("USER")))
                .andExpect(status().isNoContent());
    }

    @Test
    void submitExpense_Success() throws Exception {
        // Arrange
        Expense expense = createExpense(testUser, testCategory, new BigDecimal("100.00"));

        // Act & Assert
        mockMvc.perform(post("/api/expenses/" + expense.getId() + "/submit")
                        .with(user(testUser.getEmail()).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.submittedAt", notNullValue()));
    }

    @Test
    void getPendingExpenses_ManagerAccess_Success() throws Exception {
        // Arrange - Create submitted expense
        Expense expense = createExpense(testUser, testCategory, new BigDecimal("100.00"));
        expense.setStatus(ExpenseStatus.SUBMITTED);
        expenseRepository.save(expense);

        // Act & Assert
        mockMvc.perform(get("/api/expenses/pending")
                        .with(user(managerUser.getEmail()).roles("MANAGER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("SUBMITTED"));
    }

    @Test
    void getPendingExpenses_NonManagerAccess_ReturnsForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/expenses/pending")
                        .with(user(testUser.getEmail()).roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void approveExpense_Success() throws Exception {
        // Arrange
        Expense expense = createExpense(testUser, testCategory, new BigDecimal("100.00"));
        expense.setStatus(ExpenseStatus.SUBMITTED);
        expenseRepository.save(expense);

        ApprovalRequest approvalRequest = new ApprovalRequest();
        approvalRequest.setApproved(true);
        approvalRequest.setReviewNotes("Approved");

        // Act & Assert
        mockMvc.perform(post("/api/expenses/" + expense.getId() + "/approve")
                        .with(user(managerUser.getEmail()).roles("MANAGER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approvalRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.reviewNotes").value("Approved"))
                .andExpect(jsonPath("$.reviewedAt", notNullValue()));
    }

    @Test
    void rejectExpense_Success() throws Exception {
        // Arrange
        Expense expense = createExpense(testUser, testCategory, new BigDecimal("100.00"));
        expense.setStatus(ExpenseStatus.SUBMITTED);
        expenseRepository.save(expense);

        ApprovalRequest approvalRequest = new ApprovalRequest();
        approvalRequest.setApproved(false);
        approvalRequest.setReviewNotes("Rejected");

        // Act & Assert
        mockMvc.perform(post("/api/expenses/" + expense.getId() + "/reject")
                        .with(user(managerUser.getEmail()).roles("MANAGER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approvalRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.reviewNotes").value("Rejected"));
    }

    @Test
    void approveExpense_NonManagerAccess_ReturnsForbidden() throws Exception {
        // Arrange
        Expense expense = createExpense(testUser, testCategory, new BigDecimal("100.00"));
        expense.setStatus(ExpenseStatus.SUBMITTED);
        expenseRepository.save(expense);

        ApprovalRequest approvalRequest = new ApprovalRequest();
        approvalRequest.setApproved(true);
        approvalRequest.setReviewNotes("Approved");

        // Act & Assert
        mockMvc.perform(post("/api/expenses/" + expense.getId() + "/approve")
                        .with(user(testUser.getEmail()).roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approvalRequest)))
                .andExpect(status().isForbidden());
    }

    private Expense createExpense(User user, Category category, BigDecimal amount) {
        Expense expense = new Expense();
        expense.setUser(user);
        expense.setCategory(category);
        expense.setAmount(amount);
        expense.setExpenseDate(LocalDate.now());
        expense.setDescription("Test expense");
        expense.setStatus(ExpenseStatus.DRAFT);
        return expenseRepository.save(expense);
    }
}
