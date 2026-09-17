package com.vityarthi.cove.controller;

import com.vityarthi.cove.dao.JdbcProjectAnalyticsDao;
import com.vityarthi.cove.service.DeadlineMonitoringDaemon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controller exposing Direct JDBC Analytics and Multithreading Diagnostic Metrics.
 * Demonstrates:
 * - CSE2006 Unit 3: Multithreading diagnostics.
 * - CSE2006 Unit 5 & Indicative Experiment 14: Direct JDBC Query Execution.
 */
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final JdbcProjectAnalyticsDao jdbcAnalyticsDao;
    private final DeadlineMonitoringDaemon deadlineMonitoringDaemon;

    @Autowired
    public AnalyticsController(JdbcProjectAnalyticsDao jdbcAnalyticsDao,
                               DeadlineMonitoringDaemon deadlineMonitoringDaemon) {
        this.jdbcAnalyticsDao = jdbcAnalyticsDao;
        this.deadlineMonitoringDaemon = deadlineMonitoringDaemon;
    }

    /**
     * Raw JDBC Aggregate Query 1: Cross-project skill demand distribution.
     */
    @GetMapping("/skill-demand")
    public ResponseEntity<List<Map<String, Object>>> getSkillDemandDistribution() {
        return ResponseEntity.ok(jdbcAnalyticsDao.getSkillDemandDistribution());
    }

    /**
     * Raw JDBC Aggregate Query 2: Student project task workload and completion index.
     */
    @GetMapping("/workload")
    public ResponseEntity<List<Map<String, Object>>> getStudentWorkloadReport() {
        return ResponseEntity.ok(jdbcAnalyticsDao.getStudentWorkloadReport());
    }

    /**
     * Raw JDBC Aggregate Query 3: Executive summary platform metrics.
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getExecutiveSummary() {
        return ResponseEntity.ok(jdbcAnalyticsDao.getPlatformExecutiveSummary());
    }

    /**
     * Real-time Multithreaded Daemon Diagnostic Status.
     */
    @GetMapping("/daemon-diagnostics")
    public ResponseEntity<Map<String, Object>> getDaemonDiagnostics() {
        return ResponseEntity.ok(deadlineMonitoringDaemon.getDaemonDiagnostics());
    }
}
