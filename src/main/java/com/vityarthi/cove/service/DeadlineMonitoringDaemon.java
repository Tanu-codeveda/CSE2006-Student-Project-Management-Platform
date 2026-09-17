package com.vityarthi.cove.service;

import com.vityarthi.cove.model.Project;
import com.vityarthi.cove.model.ProjectStatus;
import com.vityarthi.cove.model.Task;
import com.vityarthi.cove.repository.ProjectRepository;
import com.vityarthi.cove.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Multithreaded Background Service for Proactive Deadline Monitoring.
 * Demonstrates:
 * - CSE2006 Unit 3 & Indicative Experiment 13: Multithreading by extending Thread class.
 * - Complete Thread Life Cycle: NEW -> RUNNABLE -> TIMED_WAITING -> TERMINATED.
 * - Thread Synchronization: synchronized status inspection and state protection.
 * - Real-world purpose: Proactively scans overdue tasks & deadlines without blocking user requests.
 */
@Service
public class DeadlineMonitoringDaemon extends Thread {

    private static final Logger log = LoggerFactory.getLogger(DeadlineMonitoringDaemon.class);

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final NotificationService notificationService;

    @Value("${cove.daemon.deadline-check-interval-ms:30000}")
    private long checkIntervalMs;

    private volatile boolean running = true;
    private long cyclesExecuted = 0;
    private LocalDateTime lastRunTimestamp;
    private int lastOverdueDetectedCount = 0;

    // Cache of already notified task IDs to prevent duplicate spamming
    private final Set<Long> alertedTaskIds = Collections.synchronizedSet(new HashSet<>());

    @Autowired
    public DeadlineMonitoringDaemon(TaskRepository taskRepository,
                                    ProjectRepository projectRepository,
                                    NotificationService notificationService) {
        super("DeadlineMonitoringDaemon-Worker");
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.notificationService = notificationService;
        // Mark as daemon so it does not prevent JVM shutdown
        setDaemon(true);
    }

    @PostConstruct
    public void init() {
        log.info("[Multithreading] Initializing Deadline Monitoring Thread. State: {}", getState());
        this.start(); // Transitions from NEW to RUNNABLE
        log.info("[Multithreading] Deadline Monitoring Thread started. State: {}", getState());
    }

    @PreDestroy
    public void cleanup() {
        log.info("[Multithreading] Shutting down Deadline Monitoring Thread gracefully...");
        this.running = false;
        this.interrupt(); // Wake thread from TIMED_WAITING
        try {
            this.join(2000); // Await thread termination
            log.info("[Multithreading] Deadline Monitoring Thread TERMINATED.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Main execution loop of the daemon thread.
     */
    @Override
    public void run() {
        log.info("[Multithreading] Deadline Monitoring Thread running in background loop...");
        while (running && !isInterrupted()) {
            try {
                executeAuditCycle();
                // TIMED_WAITING state
                Thread.sleep(checkIntervalMs);
            } catch (InterruptedException e) {
                log.info("[Multithreading] Daemon received interrupt signal. Exiting loop.");
                break;
            } catch (Exception e) {
                log.error("[Multithreading] Error during deadline monitoring cycle: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * Executes one complete audit cycle checking overdue tasks and approaching deadlines.
     */
    public synchronized void executeAuditCycle() {
        LocalDate today = LocalDate.now();
        LocalDate approachingThreshold = today.plusDays(2);

        // 1. Detect Overdue Tasks
        List<Task> overdueTasks = taskRepository.findOverdueTasks(today);
        int newlyAlerted = 0;

        for (Task task : overdueTasks) {
            if (!alertedTaskIds.contains(task.getId())) {
                alertedTaskIds.add(task.getId());
                newlyAlerted++;

                // Notify Assignee
                if (task.getAssignee() != null) {
                    notificationService.sendNotification(
                            task.getAssignee(),
                            "URGENT: Task Overdue",
                            "Task '" + task.getTitle() + "' in project '" + task.getProject().getTitle() +
                                    "' was due on " + task.getDeadline() + " and is now OVERDUE.",
                            "DEADLINE_OVERDUE"
                    );
                }

                // Notify Project Owner
                if (task.getProject().getOwner() != null && !task.getProject().getOwner().equals(task.getAssignee())) {
                    notificationService.sendNotification(
                            task.getProject().getOwner(),
                            "Project Risk: Overdue Task",
                            "Task '" + task.getTitle() + "' assigned to " +
                                    (task.getAssignee() != null ? task.getAssignee().getName() : "Unassigned") +
                                    " has exceeded its deadline.",
                            "PROJECT_ALERT"
                    );
                }
            }
        }

        // 2. Detect Approaching Deadlines (< 48 hours)
        List<Task> approachingTasks = taskRepository.findApproachingDeadlineTasks(today, approachingThreshold);
        for (Task task : approachingTasks) {
            if (!alertedTaskIds.contains(-task.getId())) { // Use negative ID to distinguish approaching
                alertedTaskIds.add(-task.getId());
                if (task.getAssignee() != null) {
                    notificationService.sendNotification(
                            task.getAssignee(),
                            "Reminder: Deadline Approaching",
                            "Task '" + task.getTitle() + "' is due in less than 48 hours (Deadline: " + task.getDeadline() + ").",
                            "DEADLINE_APPROACHING"
                    );
                }
            }
        }

        this.cyclesExecuted++;
        this.lastRunTimestamp = LocalDateTime.now();
        this.lastOverdueDetectedCount = overdueTasks.size();

        log.info("[Multithreading Cycle #{}] Audited deadlines. Overdue: {}, Approaching: {}, New alerts: {}",
                cyclesExecuted, overdueTasks.size(), approachingTasks.size(), newlyAlerted);
    }

    /**
     * Synchronized method providing real-time daemon diagnostics to dashboard.
     */
    public synchronized Map<String, Object> getDaemonDiagnostics() {
        Map<String, Object> diagnostics = new LinkedHashMap<>();
        diagnostics.put("threadName", getName());
        diagnostics.put("threadId", getId());
        diagnostics.put("threadState", getState().name());
        diagnostics.put("isAlive", isAlive());
        diagnostics.put("isDaemon", isDaemon());
        diagnostics.put("cyclesExecuted", cyclesExecuted);
        diagnostics.put("lastRunTimestamp", lastRunTimestamp);
        diagnostics.put("checkIntervalMs", checkIntervalMs);
        diagnostics.put("lastOverdueCount", lastOverdueDetectedCount);
        diagnostics.put("totalAlertsDispatched", alertedTaskIds.size());
        return diagnostics;
    }
}
