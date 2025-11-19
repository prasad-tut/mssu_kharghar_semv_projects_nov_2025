package com.example.bugtracker.model;

import java.time.LocalDate;

/**
 * Bug Model Class
 * Represents a bug report in the tracking system
 */
public class Bug {
    
    private Integer id;
    private String description;
    private String priority;           // HIGH, MEDIUM, LOW
    private String severity;           // CRITICAL, MARGINAL, NEGLIGIBLE
    private LocalDate detectedOn;
    private String assignedTo;
    private String status;             // OPEN, IN_PROGRESS, FIXED

    // Constructors
    public Bug() {
    }

    public Bug(String description, String priority, String severity, 
               LocalDate detectedOn, String assignedTo) {
        this.description = description;
        this.priority = priority;
        this.severity = severity;
        this.detectedOn = detectedOn;
        this.assignedTo = assignedTo;
        this.status = "OPEN";
    }

    public Bug(Integer id, String description, String priority, String severity,
               LocalDate detectedOn, String assignedTo, String status) {
        this.id = id;
        this.description = description;
        this.priority = priority;
        this.severity = severity;
        this.detectedOn = detectedOn;
        this.assignedTo = assignedTo;
        this.status = status;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public LocalDate getDetectedOn() {
        return detectedOn;
    }

    public void setDetectedOn(LocalDate detectedOn) {
        this.detectedOn = detectedOn;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Bug{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", priority='" + priority + '\'' +
                ", severity='" + severity + '\'' +
                ", detectedOn=" + detectedOn +
                ", assignedTo='" + assignedTo + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
