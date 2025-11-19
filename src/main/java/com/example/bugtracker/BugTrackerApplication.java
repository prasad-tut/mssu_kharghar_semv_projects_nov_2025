package com.example.bugtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bug Tracker System - Main Application Class
 * Full-stack Bug Tracking System with Spring Boot and AWS RDS MySQL
 */
@SpringBootApplication
public class BugTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BugTrackerApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("🐛 Bug Tracker System Started Successfully");
        System.out.println("📍 Access at: http://localhost:8080");
        System.out.println("📊 All Bugs: http://localhost:8080/bugs");
        System.out.println("📝 Report Bug: http://localhost:8080/raise-bug");
        System.out.println("========================================\n");
    }
}
