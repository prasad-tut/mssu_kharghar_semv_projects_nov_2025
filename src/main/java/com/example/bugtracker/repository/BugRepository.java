package com.example.bugtracker.repository;

import com.example.bugtracker.model.Bug;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Bug Repository Class
 * Handles all database operations for Bug entity using JDBC Template
 */
@Repository
public class BugRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper for mapping ResultSet to Bug object
    private final RowMapper<Bug> bugRowMapper = new RowMapper<Bug>() {
        @Override
        public Bug mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Bug(
                    rs.getInt("id"),
                    rs.getString("description"),
                    rs.getString("priority"),
                    rs.getString("severity"),
                    rs.getDate("detected_on").toLocalDate(),
                    rs.getString("assigned_to"),
                    rs.getString("status")
            );
        }
    };

    /**
     * Insert a new bug into the database
     * @param bug Bug object to insert
     * @return number of rows affected
     */
    public int insertBug(Bug bug) {
        String sql = "INSERT INTO bugs (description, priority, severity, detected_on, assigned_to, status) " +
                     "VALUES (?, ?, ?, ?, ?, 'OPEN')";
        return jdbcTemplate.update(sql, 
                bug.getDescription(), 
                bug.getPriority(), 
                bug.getSeverity(), 
                Date.valueOf(bug.getDetectedOn()),
                bug.getAssignedTo());
    }

    /**
     * Get all bugs from the database
     * @return List of all bugs
     */
    public List<Bug> getAllBugs() {
        String sql = "SELECT id, description, priority, severity, detected_on, assigned_to, status " +
                     "FROM bugs ORDER BY id DESC";
        return jdbcTemplate.query(sql, bugRowMapper);
    }

    /**
     * Get a specific bug by ID
     * @param id Bug ID
     * @return Bug object or null if not found
     */
    public Bug getBugById(Integer id) {
        String sql = "SELECT id, description, priority, severity, detected_on, assigned_to, status " +
                     "FROM bugs WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, bugRowMapper);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Update the status of a bug
     * @param id Bug ID
     * @param status New status
     * @return number of rows affected
     */
    public int updateBugStatus(Integer id, String status) {
        String sql = "UPDATE bugs SET status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status, id);
    }

    /**
     * Delete a bug by ID
     * @param id Bug ID
     * @return number of rows affected
     */
    public int deleteBug(Integer id) {
        String sql = "DELETE FROM bugs WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * Get count of bugs by status
     * @param status Status to filter by
     * @return count of bugs with given status
     */
    public long getCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM bugs WHERE status = ?";
        Long count = jdbcTemplate.queryForObject(sql, new Object[]{status}, Long.class);
        return count != null ? count : 0;
    }

    /**
     * Get total count of bugs
     * @return total bug count
     */
    public long getTotalBugCount() {
        String sql = "SELECT COUNT(*) FROM bugs";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }

    /**
     * Get bugs by priority
     * @param priority Priority to filter by
     * @return List of bugs with given priority
     */
    public List<Bug> getBugsByPriority(String priority) {
        String sql = "SELECT id, description, priority, severity, detected_on, assigned_to, status " +
                     "FROM bugs WHERE priority = ? ORDER BY detected_on DESC";
        return jdbcTemplate.query(sql, new Object[]{priority}, bugRowMapper);
    }

    /**
     * Get bugs by severity
     * @param severity Severity to filter by
     * @return List of bugs with given severity
     */
    public List<Bug> getBugsBySeverity(String severity) {
        String sql = "SELECT id, description, priority, severity, detected_on, assigned_to, status " +
                     "FROM bugs WHERE severity = ? ORDER BY detected_on DESC";
        return jdbcTemplate.query(sql, new Object[]{severity}, bugRowMapper);
    }

    /**
     * Get bugs by status
     * @param status Status to filter by
     * @return List of bugs with given status
     */
    public List<Bug> getBugsByStatus(String status) {
        String sql = "SELECT id, description, priority, severity, detected_on, assigned_to, status " +
                     "FROM bugs WHERE status = ? ORDER BY id DESC";
        return jdbcTemplate.query(sql, new Object[]{status}, bugRowMapper);
    }

    /**
     * Get bugs assigned to a specific person
     * @param assignedTo Person name
     * @return List of bugs assigned to that person
     */
    public List<Bug> getBugsByAssignee(String assignedTo) {
        String sql = "SELECT id, description, priority, severity, detected_on, assigned_to, status " +
                     "FROM bugs WHERE assigned_to = ? ORDER BY id DESC";
        return jdbcTemplate.query(sql, new Object[]{assignedTo}, bugRowMapper);
    }
}
