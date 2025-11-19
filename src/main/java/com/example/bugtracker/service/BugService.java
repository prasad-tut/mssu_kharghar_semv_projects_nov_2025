package com.example.bugtracker.service;

import com.example.bugtracker.model.Bug;
import com.example.bugtracker.repository.BugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Bug Service Class
 * Contains business logic for bug tracking operations
 */
@Service
public class BugService {

    @Autowired
    private BugRepository bugRepository;

    /**
     * Create a new bug report
     * @param bug Bug object to create
     * @return success status
     */
    public boolean createBug(Bug bug) {
        if (bug == null || bug.getDescription() == null || bug.getDescription().trim().isEmpty()) {
            return false;
        }
        if (bug.getPriority() == null || bug.getSeverity() == null || bug.getDetectedOn() == null) {
            return false;
        }
        int result = bugRepository.insertBug(bug);
        return result > 0;
    }

    /**
     * Get all bugs
     * @return List of all bugs
     */
    public List<Bug> getAllBugs() {
        return bugRepository.getAllBugs();
    }

    /**
     * Get a specific bug by ID
     * @param id Bug ID
     * @return Bug object or null if not found
     */
    public Bug getBugById(Integer id) {
        if (id == null || id <= 0) {
            return null;
        }
        return bugRepository.getBugById(id);
    }

    /**
     * Update bug status
     * @param id Bug ID
     * @param status New status
     * @return success status
     */
    public boolean updateBugStatus(Integer id, String status) {
        if (id == null || id <= 0 || status == null || status.trim().isEmpty()) {
            return false;
        }
        
        // Validate status
        if (!isValidStatus(status)) {
            return false;
        }
        
        Bug existingBug = bugRepository.getBugById(id);
        if (existingBug == null) {
            return false;
        }
        
        int result = bugRepository.updateBugStatus(id, status);
        return result > 0;
    }

    /**
     * Delete a bug
     * @param id Bug ID
     * @return success status
     */
    public boolean deleteBug(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        int result = bugRepository.deleteBug(id);
        return result > 0;
    }

    /**
     * Get count of bugs by status
     * @param status Status to filter by
     * @return count of bugs
     */
    public long getCountByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return 0;
        }
        return bugRepository.getCountByStatus(status);
    }

    /**
     * Get total count of bugs
     * @return total bug count
     */
    public long getTotalBugCount() {
        return bugRepository.getTotalBugCount();
    }

    /**
     * Get bugs by priority
     * @param priority Priority to filter by
     * @return List of bugs
     */
    public List<Bug> getBugsByPriority(String priority) {
        if (priority == null || !isValidPriority(priority)) {
            return List.of();
        }
        return bugRepository.getBugsByPriority(priority);
    }

    /**
     * Get bugs by severity
     * @param severity Severity to filter by
     * @return List of bugs
     */
    public List<Bug> getBugsBySeverity(String severity) {
        if (severity == null || !isValidSeverity(severity)) {
            return List.of();
        }
        return bugRepository.getBugsBySeverity(severity);
    }

    /**
     * Get bugs by status
     * @param status Status to filter by
     * @return List of bugs
     */
    public List<Bug> getBugsByStatus(String status) {
        if (status == null || !isValidStatus(status)) {
            return List.of();
        }
        return bugRepository.getBugsByStatus(status);
    }

    /**
     * Get bugs assigned to a specific person
     * @param assignedTo Person name
     * @return List of bugs
     */
    public List<Bug> getBugsByAssignee(String assignedTo) {
        if (assignedTo == null || assignedTo.trim().isEmpty()) {
            return List.of();
        }
        return bugRepository.getBugsByAssignee(assignedTo);
    }

    /**
     * Validate priority value
     * @param priority Priority to validate
     * @return true if valid
     */
    private boolean isValidPriority(String priority) {
        return priority.equals("HIGH") || priority.equals("MEDIUM") || priority.equals("LOW");
    }

    /**
     * Validate severity value
     * @param severity Severity to validate
     * @return true if valid
     */
    private boolean isValidSeverity(String severity) {
        return severity.equals("CRITICAL") || severity.equals("MARGINAL") || severity.equals("NEGLIGIBLE");
    }

    /**
     * Validate status value
     * @param status Status to validate
     * @return true if valid
     */
    private boolean isValidStatus(String status) {
        return status.equals("OPEN") || status.equals("IN_PROGRESS") || status.equals("FIXED");
    }
}
