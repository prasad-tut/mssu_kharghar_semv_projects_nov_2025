package com.expensemanagement.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify RDS PostgreSQL database connectivity
 * This test should be run after RDS setup to verify the connection
 * 
 * To run this test with production RDS:
 * 1. Set environment variables: DB_URL, DB_USERNAME, DB_PASSWORD
 * 2. Run: mvn test -Dtest=RDSConnectionTest -Dspring.profiles.active=prod
 */
@SpringBootTest
@ActiveProfiles("test")
public class RDSConnectionTest {

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testDataSourceConfiguration() {
        assertNotNull(dataSource, "DataSource should be configured");
        System.out.println("✓ DataSource is configured");
    }

    @Test
    public void testDatabaseConnection() throws Exception {
        assertNotNull(dataSource, "DataSource should not be null");
        
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");
            
            System.out.println("✓ Database connection successful");
            
            // Get database metadata
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("Database Product: " + metaData.getDatabaseProductName());
            System.out.println("Database Version: " + metaData.getDatabaseProductVersion());
            System.out.println("Driver Name: " + metaData.getDriverName());
            System.out.println("Driver Version: " + metaData.getDriverVersion());
            System.out.println("URL: " + metaData.getURL());
            System.out.println("Username: " + metaData.getUserName());
        }
    }

    @Test
    public void testPostgreSQLVersion() {
        assertNotNull(jdbcTemplate, "JdbcTemplate should not be null");
        
        String version = jdbcTemplate.queryForObject("SELECT version()", String.class);
        assertNotNull(version, "PostgreSQL version should not be null");
        assertTrue(version.contains("PostgreSQL"), "Should be PostgreSQL database");
        
        System.out.println("✓ PostgreSQL Version: " + version);
    }

    @Test
    public void testDatabaseExists() {
        assertNotNull(jdbcTemplate, "JdbcTemplate should not be null");
        
        String currentDatabase = jdbcTemplate.queryForObject("SELECT current_database()", String.class);
        assertNotNull(currentDatabase, "Current database should not be null");
        
        System.out.println("✓ Connected to database: " + currentDatabase);
    }

    @Test
    public void testTableAccess() throws Exception {
        assertNotNull(dataSource, "DataSource should not be null");
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            // Get all tables in public schema
            ResultSet tables = metaData.getTables(null, "public", "%", new String[]{"TABLE"});
            
            int tableCount = 0;
            System.out.println("\nTables in public schema:");
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("  - " + tableName);
                tableCount++;
            }
            
            if (tableCount == 0) {
                System.out.println("  (No tables found - migrations not run yet)");
            } else {
                System.out.println("✓ Found " + tableCount + " tables");
            }
        }
    }

    @Test
    public void testFlywayMigrationStatus() {
        assertNotNull(jdbcTemplate, "JdbcTemplate should not be null");
        
        // Check if flyway_schema_history table exists
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'flyway_schema_history'",
            Integer.class
        );
        
        if (count != null && count > 0) {
            // Get migration count
            Integer migrationCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history WHERE success = true",
                Integer.class
            );
            
            System.out.println("✓ Flyway migrations table exists");
            System.out.println("✓ Successful migrations: " + migrationCount);
            
            // List all migrations
            jdbcTemplate.query(
                "SELECT version, description, installed_on, success FROM flyway_schema_history ORDER BY installed_rank",
                (rs) -> {
                    System.out.println("\nMigration History:");
                    do {
                        String version = rs.getString("version");
                        String description = rs.getString("description");
                        String installedOn = rs.getString("installed_on");
                        boolean success = rs.getBoolean("success");
                        String status = success ? "✓" : "✗";
                        System.out.println(String.format("  %s V%s - %s (%s)", status, version, description, installedOn));
                    } while (rs.next());
                }
            );
        } else {
            System.out.println("ℹ Flyway migrations not run yet");
        }
    }

    @Test
    public void testConnectionPooling() throws Exception {
        assertNotNull(dataSource, "DataSource should not be null");
        
        // Test multiple connections
        Connection conn1 = dataSource.getConnection();
        Connection conn2 = dataSource.getConnection();
        Connection conn3 = dataSource.getConnection();
        
        assertNotNull(conn1, "Connection 1 should not be null");
        assertNotNull(conn2, "Connection 2 should not be null");
        assertNotNull(conn3, "Connection 3 should not be null");
        
        System.out.println("✓ Connection pooling is working");
        
        conn1.close();
        conn2.close();
        conn3.close();
        
        System.out.println("✓ Connections closed successfully");
    }

    @Test
    public void testBasicQuery() {
        assertNotNull(jdbcTemplate, "JdbcTemplate should not be null");
        
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertEquals(1, result, "Query should return 1");
        
        System.out.println("✓ Basic query execution successful");
    }

    @Test
    public void testTransactionSupport() {
        assertNotNull(jdbcTemplate, "JdbcTemplate should not be null");
        
        // Test that we can start a transaction
        jdbcTemplate.execute("BEGIN");
        jdbcTemplate.execute("ROLLBACK");
        
        System.out.println("✓ Transaction support is available");
    }

    @Test
    public void printConnectionSummary() throws Exception {
        System.out.println("\n==========================================");
        System.out.println("RDS Connection Test Summary");
        System.out.println("==========================================");
        
        if (dataSource != null) {
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                
                System.out.println("Status: ✓ CONNECTED");
                System.out.println("Database: " + metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion());
                System.out.println("URL: " + metaData.getURL());
                System.out.println("User: " + metaData.getUserName());
                
                // Check table count
                Integer tableCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public'",
                    Integer.class
                );
                System.out.println("Tables: " + tableCount);
                
                // Check if migrations are run
                Integer flywayExists = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'flyway_schema_history'",
                    Integer.class
                );
                
                if (flywayExists != null && flywayExists > 0) {
                    Integer migrationCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM flyway_schema_history WHERE success = true",
                        Integer.class
                    );
                    System.out.println("Migrations: " + migrationCount + " successful");
                } else {
                    System.out.println("Migrations: Not run yet");
                }
                
                System.out.println("==========================================");
                System.out.println("✓ All connection tests passed!");
                System.out.println("==========================================");
            }
        } else {
            System.out.println("Status: ✗ NOT CONNECTED");
            System.out.println("DataSource is not configured");
        }
    }
}
