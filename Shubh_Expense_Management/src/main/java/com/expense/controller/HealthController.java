package com.expense.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for health check operations.
 * Provides endpoints for monitoring application health and status.
 */
@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Application health check endpoints")
public class HealthController {
    
    /**
     * Health check endpoint to verify the application is running.
     * This endpoint is publicly accessible and does not require authentication.
     * 
     * @return ResponseEntity containing health status information
     */
    @GetMapping
    @Operation(
            summary = "Health check", 
            description = "Check if the application is running and responsive. Returns basic health status information."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application is healthy and running")
    })
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "Expense Management API");
        health.put("version", "1.0.0");
        
        return ResponseEntity.ok(health);
    }
}
