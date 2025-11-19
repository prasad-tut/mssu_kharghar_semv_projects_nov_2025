package com.expense.controller;

import com.expense.dto.AuthResponse;
import com.expense.dto.LoginRequest;
import com.expense.dto.RegisterRequest;
import com.expense.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 * Handles user registration, login, and token refresh operations.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * Register a new user account.
     * Endpoint: POST /api/auth/register
     *
     * @param registerRequest the registration request with user details
     * @return ResponseEntity with AuthResponse containing token and user info
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registration request received for email: {}", registerRequest.getEmail());
        
        AuthResponse response = authService.register(registerRequest);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Authenticate user and generate JWT token.
     * Endpoint: POST /api/auth/login
     *
     * @param loginRequest the login request with credentials
     * @return ResponseEntity with AuthResponse containing token and user info
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request received for email: {}", loginRequest.getEmail());
        
        AuthResponse response = authService.login(loginRequest);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Refresh JWT token for authenticated user.
     * Endpoint: POST /api/auth/refresh
     *
     * @param authentication the current authentication object
     * @return ResponseEntity with new JWT token
     */
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(Authentication authentication) {
        log.info("Token refresh request received for user: {}", authentication.getName());
        
        String newToken = authService.refreshToken(authentication.getName());
        
        return ResponseEntity.ok(newToken);
    }
}
