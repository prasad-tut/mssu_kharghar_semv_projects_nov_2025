package com.expense.service;

import com.expense.dto.AuthResponse;
import com.expense.dto.LoginRequest;
import com.expense.dto.RegisterRequest;
import com.expense.dto.UserResponse;
import com.expense.model.User;
import com.expense.model.UserRole;
import com.expense.repository.UserRepository;
import com.expense.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for authentication operations.
 * Handles user registration, login, and token management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    
    /**
     * Register a new user with the provided information.
     * Validates that email is not already in use and hashes the password.
     *
     * @param registerRequest the registration request containing user details
     * @return AuthResponse with JWT token and user information
     * @throws IllegalArgumentException if email is already registered
     */
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        log.info("Attempting to register user with email: {}", registerRequest.getEmail());
        
        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            log.warn("Registration failed: Email already exists - {}", registerRequest.getEmail());
            throw new IllegalArgumentException("Email is already registered");
        }
        
        // Create new user entity
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setRole(UserRole.USER);
        
        // Save user to database
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        
        // Generate JWT token
        String token = jwtTokenProvider.generateTokenFromUsername(savedUser.getEmail());
        
        // Create response
        UserResponse userResponse = mapToUserResponse(savedUser);
        return new AuthResponse(token, null, userResponse);
    }
    
    /**
     * Authenticate user with email and password.
     * Validates credentials and generates JWT token.
     *
     * @param loginRequest the login request containing credentials
     * @return AuthResponse with JWT token and user information
     * @throws org.springframework.security.core.AuthenticationException if credentials are invalid
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest loginRequest) {
        log.info("Attempting to authenticate user: {}", loginRequest.getEmail());
        
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generate JWT token
        String token = jwtTokenProvider.generateToken(authentication);
        
        // Get user details
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));
        
        log.info("User authenticated successfully: {}", user.getEmail());
        
        // Create response
        UserResponse userResponse = mapToUserResponse(user);
        return new AuthResponse(token, null, userResponse);
    }
    
    /**
     * Refresh JWT token for authenticated user.
     * Generates a new token with extended expiration.
     *
     * @param email the email of the user requesting token refresh
     * @return new JWT token
     */
    public String refreshToken(String email) {
        log.info("Refreshing token for user: {}", email);
        
        // Verify user exists
        userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Generate new token
        return jwtTokenProvider.generateTokenFromUsername(email);
    }
    
    /**
     * Map User entity to UserResponse DTO.
     *
     * @param user the user entity
     * @return UserResponse DTO
     */
    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        );
    }
}
