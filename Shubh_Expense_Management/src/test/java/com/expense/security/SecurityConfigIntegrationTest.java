package com.expense.security;

import com.expense.model.Category;
import com.expense.model.User;
import com.expense.model.UserRole;
import com.expense.repository.CategoryRepository;
import com.expense.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private User managerUser;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    void publicEndpoints_NoAuthentication_Success() throws Exception {
        // Health endpoint should be accessible without authentication
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoints_NoAuthentication_ReturnsUnauthorized() throws Exception {
        // Expenses endpoint should require authentication
        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoints_WithAuthentication_Success() throws Exception {
        // Expenses endpoint should work with authentication
        mockMvc.perform(get("/api/expenses")
                        .with(user(testUser.getEmail()).roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void managerEndpoints_UserRole_ReturnsForbidden() throws Exception {
        // Pending expenses endpoint should require MANAGER role
        mockMvc.perform(get("/api/expenses/pending")
                        .with(user(testUser.getEmail()).roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void managerEndpoints_ManagerRole_Success() throws Exception {
        // Pending expenses endpoint should work with MANAGER role
        mockMvc.perform(get("/api/expenses/pending")
                        .with(user(managerUser.getEmail()).roles("MANAGER")))
                .andExpect(status().isOk());
    }

    @Test
    void categoriesEndpoint_WithAuthentication_Success() throws Exception {
        // Categories endpoint should work with authentication
        mockMvc.perform(get("/api/categories")
                        .with(user(testUser.getEmail()).roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void categoriesEndpoint_NoAuthentication_ReturnsUnauthorized() throws Exception {
        // Categories endpoint should require authentication
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void swaggerEndpoints_NoAuthentication_Success() throws Exception {
        // Swagger UI should be accessible without authentication
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void apiDocsEndpoint_NoAuthentication_Success() throws Exception {
        // API docs should be accessible without authentication
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }
}
