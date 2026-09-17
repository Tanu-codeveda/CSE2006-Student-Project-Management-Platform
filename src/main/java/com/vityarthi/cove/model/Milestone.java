package com.vityarthi.cove.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Milestone entity tracking structured academic phases of a project.
 * Examples: Requirement Analysis, System Design, Implementation, Testing, Final Presentation.
 */
@Entity
@Table(name = "milestones")
public class Milestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(length = 500)
    private String description;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "target_deadline", nullable = false)
    private LocalDate targetDeadline;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Column(name = "weight_percentage", nullable = false)
    private double weightPercentage = 20.0;

    public Milestone() {}

    public Milestone(Project project, String title, String description, LocalDate targetDeadline, double weightPercentage) {
        this.project = Objects.requireNonNull(project, "Project must not be null");
        this.title = Objects.requireNonNull(title, "Title must not be null");
        this.description = description;
        this.targetDeadline = Objects.requireNonNull(targetDeadline, "Deadline must not be null");
        this.weightPercentage = Math.max(0.0, weightPercentage);
    }

    public void markComplete() {
        this.completed = true;
        this.completionDate = LocalDateTime.now();
    }

    public void markIncomplete() {
        this.completed = false;
        this.completionDate = null;
    }

    public boolean isOverdue() {
        if (completed) return false;
        return targetDeadline != null && targetDeadline.isBefore(LocalDate.now());
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

    public LocalDate getTargetDeadline() {
        return targetDeadline;
    }

    public void setTargetDeadline(LocalDate targetDeadline) {
        this.targetDeadline = targetDeadline;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    public double getWeightPercentage() {
        return weightPercentage;
    }

    public void setWeightPercentage(double weightPercentage) {
        this.weightPercentage = weightPercentage;
    }
}
