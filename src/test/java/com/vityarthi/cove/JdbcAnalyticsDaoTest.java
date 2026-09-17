package com.vityarthi.cove;

import com.vityarthi.cove.dao.JdbcProjectAnalyticsDao;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test verifying direct JDBC analytics execution and metadata extraction.
 * Demonstrates:
 * - CSE2006 Unit 5 & Indicative Experiment 14: Direct JDBC execution.
 */
public class JdbcAnalyticsDaoTest {

    private JdbcProjectAnalyticsDao dao;
    private JdbcDataSource dataSource;

    @BeforeEach
    void setUp() throws Exception {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:jdbctest;DB_CLOSE_DELAY=-1;MODE=MySQL");
        dataSource.setUser("sa");
        dataSource.setPassword("");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP ALL OBJECTS");

            stmt.execute("CREATE TABLE users (id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(100));");
            stmt.execute("CREATE TABLE students (user_id BIGINT PRIMARY KEY, registration_number VARCHAR(30));");
            stmt.execute("CREATE TABLE skills (id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(80), category VARCHAR(30));");
            stmt.execute("CREATE TABLE projects (id BIGINT PRIMARY KEY AUTO_INCREMENT, title VARCHAR(100), status VARCHAR(30));");
            stmt.execute("CREATE TABLE project_skills (id BIGINT PRIMARY KEY AUTO_INCREMENT, project_id BIGINT, skill_id BIGINT, weight DOUBLE);");
            stmt.execute("CREATE TABLE tasks (id BIGINT PRIMARY KEY AUTO_INCREMENT, title VARCHAR(100), assignee_id BIGINT, status VARCHAR(20), deadline DATE);");

            // Seed sample records
            stmt.execute("INSERT INTO users (id, name) VALUES (1, 'Alice');");
            stmt.execute("INSERT INTO students (user_id, registration_number) VALUES (1, '21BCE1001');");
            stmt.execute("INSERT INTO skills (id, name, category) VALUES (1, 'Java', 'PROGRAMMING'), (2, 'SQL', 'DATABASE');");
            stmt.execute("INSERT INTO projects (id, title, status) VALUES (1, 'Test Project', 'IN_PROGRESS');");
            stmt.execute("INSERT INTO project_skills (project_id, skill_id, weight) VALUES (1, 1, 1.5), (1, 2, 1.0);");
            stmt.execute("INSERT INTO tasks (title, assignee_id, status, deadline) VALUES ('Task 1', 1, 'COMPLETED', '2026-09-01'), ('Task 2', 1, 'TODO', '2026-10-01');");
        }

        dao = new JdbcProjectAnalyticsDao(dataSource);
    }

    @Test
    @DisplayName("Should execute raw JDBC skill demand aggregate query and extract ResultSetMetaData")
    void testGetSkillDemandDistribution() {
        List<Map<String, Object>> list = dao.getSkillDemandDistribution();

        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertEquals(2, list.size());

        Map<String, Object> first = list.get(0);
        assertTrue(first.containsKey("skill_name"));
        assertTrue(first.containsKey("project_count"));
        assertEquals("Java", first.get("skill_name"));
        assertEquals(1L, ((Number) first.get("project_count")).longValue());
    }

    @Test
    @DisplayName("Should execute raw JDBC student workload report and compute completion percentages")
    void testGetStudentWorkloadReport() {
        List<Map<String, Object>> report = dao.getStudentWorkloadReport();

        assertNotNull(report);
        assertEquals(1, report.size());

        Map<String, Object> studentRow = report.get(0);
        assertEquals("Alice", studentRow.get("student_name"));
        assertEquals(2L, ((Number) studentRow.get("total_tasks")).longValue());
        assertEquals(1L, ((Number) studentRow.get("completed_tasks")).longValue());
        assertEquals(50.0, (Double) studentRow.get("completion_rate_percentage"));
    }
}
