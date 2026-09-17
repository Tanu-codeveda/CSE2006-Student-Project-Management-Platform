package com.vityarthi.cove.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vityarthi.cove.exception.InvalidTaskStateException;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Task entity managing task details, priority scheduling, and state transitions.
 * Demonstrates:
 * - CSE2006 Unit 2: Encapsulation, constructor overloading.
 * - CSE2006 Unit 3: Exception handling during invalid transitions.
 * - CSE2006 Unit 5: JPA mappings.
 */
@Entity
@Table(name = "tasks")
public class Task implements Comparable<Task> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 1000)
    private String description;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assignee_id")
    private Student assignee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status = TaskStatus.TODO;

    @Column(nullable = false)
    private LocalDate deadline;

    @Column
    private LocalDateTime completedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    private Long version;

    public Task() {
        this.createdAt = LocalDateTime.now();
        this.status = TaskStatus.TODO;
        this.priority = TaskPriority.MEDIUM;
    }

    public Task(Project project, String title, String description, Student assignee, TaskPriority priority, LocalDate deadline) {
        this();
        this.project = Objects.requireNonNull(project, "Project is mandatory for task");
        this.title = Objects.requireNonNull(title, "Task title is mandatory");
        this.description = description;
        this.assignee = assignee;
        this.priority = priority != null ? priority : TaskPriority.MEDIUM;
        this.deadline = Objects.requireNonNull(deadline, "Deadline is mandatory");
    }

    /**
     * Validates and performs state transition.
     * Throws InvalidTaskStateException if transition is disallowed.
     */
    public synchronized void transitionTo(TaskStatus next) {
        Objects.requireNonNull(next, "Target status cannot be null");
        if (!this.status.canTransitionTo(next)) {
            throw new InvalidTaskStateException(this.status, next);
        }
        this.status = next;
        if (next == TaskStatus.COMPLETED) {
            this.completedAt = LocalDateTime.now();
        } else {
            this.completedAt = null;
        }
    }

    public boolean isOverdue() {
        if (status == TaskStatus.COMPLETED) return false;
        return deadline != null && deadline.isBefore(LocalDate.now());
    }

    @Override
    public int compareTo(Task other) {
        if (other == null) return 1;
        // Priority first (higher severity comes first in PriorityQueue)
        int priorityDiff = Integer.compare(other.priority.getSeverity(), this.priority.getSeverity());
        if (priorityDiff != 0) return priorityDiff;
        // Then earlier deadline
        return this.deadline.compareTo(other.deadline);
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Student getAssignee() {
        return assignee;
    }

    public void setAssignee(Student assignee) {
        this.assignee = assignee;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
