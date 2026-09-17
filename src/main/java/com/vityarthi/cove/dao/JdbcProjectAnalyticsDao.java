package com.vityarthi.cove.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * Direct JDBC Data Access Object for High-Performance Analytics & Reporting.
 * Demonstrates:
 * - CSE2006 Unit 5 & Indicative Experiment 14: Defining layout of JDBC API,
 *   connecting via Driver/DataSource, submitting PreparedStatement queries,
 *   iterating through ResultSet, and reading ResultSetMetaData.
 */
@Repository
public class JdbcProjectAnalyticsDao {

    private final DataSource dataSource;

    @Autowired
    public JdbcProjectAnalyticsDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Executes raw JDBC query to compute cross-project skill market demand.
     * Demonstrates PreparedStatement, ResultSet, and ResultSetMetaData.
     */
    public List<Map<String, Object>> getSkillDemandDistribution() {
        String sql = "SELECT s.name AS skill_name, s.category AS skill_category, " +
                "COUNT(ps.id) AS project_count, " +
                "AVG(ps.weight) AS avg_weight " +
                "FROM skills s " +
                "LEFT JOIN project_skills ps ON s.id = ps.skill_id " +
                "GROUP BY s.id, s.name, s.category " +
                "ORDER BY project_count DESC, s.name ASC";

        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                results.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Direct JDBC failure during skill demand analysis: " + e.getMessage(), e);
        }

        return results;
    }

    /**
     * Executes direct JDBC aggregate query to compute student team workload and task completion rate.
     */
    public List<Map<String, Object>> getStudentWorkloadReport() {
        String sql = "SELECT u.id AS student_id, u.name AS student_name, st.registration_number, " +
                "COUNT(t.id) AS total_tasks, " +
                "SUM(CASE WHEN t.status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_tasks, " +
                "SUM(CASE WHEN t.status <> 'COMPLETED' AND t.deadline < CURRENT_DATE() THEN 1 ELSE 0 END) AS overdue_tasks " +
                "FROM students st " +
                "JOIN users u ON st.user_id = u.id " +
                "LEFT JOIN tasks t ON st.user_id = t.assignee_id " +
                "GROUP BY u.id, u.name, st.registration_number " +
                "ORDER BY total_tasks DESC, completed_tasks DESC";

        List<Map<String, Object>> report = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int colCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> record = new LinkedHashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    record.put(metaData.getColumnLabel(i), rs.getObject(i));
                }
                long total = ((Number) record.getOrDefault("total_tasks", 0)).longValue();
                long completed = ((Number) record.getOrDefault("completed_tasks", 0)).longValue();
                double completionRate = (total > 0) ? (completed * 100.0 / total) : 0.0;
                record.put("completion_rate_percentage", Math.round(completionRate * 10.0) / 10.0);
                report.add(record);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Direct JDBC failure during student workload report: " + e.getMessage(), e);
        }

        return report;
    }

    /**
     * Computes platform-wide executive summary metrics using direct JDBC aggregate queries.
     */
    public Map<String, Object> getPlatformExecutiveSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();

        String sqlProjects = "SELECT status, COUNT(*) AS cnt FROM projects GROUP BY status";
        String sqlTasks = "SELECT " +
                "COUNT(*) AS total_tasks, " +
                "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_tasks, " +
                "SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS in_progress_tasks, " +
                "SUM(CASE WHEN status <> 'COMPLETED' AND deadline < CURRENT_DATE() THEN 1 ELSE 0 END) AS overdue_tasks " +
                "FROM tasks";

        try (Connection conn = dataSource.getConnection()) {
            // Task aggregates
            try (PreparedStatement stmtTasks = conn.prepareStatement(sqlTasks);
                 ResultSet rsTasks = stmtTasks.executeQuery()) {
                if (rsTasks.next()) {
                    summary.put("totalTasks", rsTasks.getLong("total_tasks"));
                    summary.put("completedTasks", rsTasks.getLong("completed_tasks"));
                    summary.put("inProgressTasks", rsTasks.getLong("in_progress_tasks"));
                    summary.put("overdueTasks", rsTasks.getLong("overdue_tasks"));
                }
            }

            // Project status distribution
            Map<String, Long> statusBreakdown = new LinkedHashMap<>();
            try (PreparedStatement stmtProj = conn.prepareStatement(sqlProjects);
                 ResultSet rsProj = stmtProj.executeQuery()) {
                while (rsProj.next()) {
                    statusBreakdown.put(rsProj.getString("status"), rsProj.getLong("cnt"));
                }
            }
            summary.put("projectStatusDistribution", statusBreakdown);

        } catch (SQLException e) {
            throw new RuntimeException("Direct JDBC error fetching executive summary: " + e.getMessage(), e);
        }

        return summary;
    }
}
