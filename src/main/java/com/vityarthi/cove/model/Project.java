package com.vityarthi.cove.model;

import com.vityarthi.cove.exception.InvalidProjectStateException;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Core Project Entity representing a student academic project.
 * Demonstrates:
 * - CSE2006 Unit 2: Encapsulation, constructor overloading, business logic methods.
 * - CSE2006 Unit 4: Collections Framework (Set, List, Map usage).
 * - CSE2006 Unit 5: JPA Relationships (@OneToOne, @OneToMany, @ManyToOne).
 */
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private Student owner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mentor_id")
    private Faculty mentor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjectStatus status = ProjectStatus.IDEA;

    @Column(name = "team_size_limit", nullable = false)
    private int teamSizeLimit = 4;

    @Column(length = 255)
    private String techStack;

    @Column(nullable = false)
    private LocalDate deadline;

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Team team;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<ProjectSkill> requiredSkills = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Milestone> milestones = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ActivityLog> activityLogs = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Project() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = ProjectStatus.IDEA;
    }

    public Project(String title, String description, Student owner, int teamSizeLimit, LocalDate deadline) {
        this();
        this.title = Objects.requireNonNull(title, "Project title is mandatory");
        this.description = Objects.requireNonNull(description, "Project description is mandatory");
        this.owner = Objects.requireNonNull(owner, "Project owner is mandatory");
        this.teamSizeLimit = Math.max(1, teamSizeLimit);
        this.deadline = Objects.requireNonNull(deadline, "Project deadline is mandatory");
        
        // Initialize 1-to-1 team with owner as Lead
        this.team = new Team(this, this.teamSizeLimit);
        this.team.addMemberSynchronized(owner, "Project Lead");
    }

    // Overloaded Constructor with techStack
    public Project(String title, String description, Student owner, int teamSizeLimit, String techStack, LocalDate deadline) {
        this(title, description, owner, teamSizeLimit, deadline);
        this.techStack = techStack;
    }

    /**
     * Enforces valid state transitions according to project status state machine.
     * Demonstrates CSE2006 Unit 1 & 3: Flow control and custom exception throwing.
     */
    public void validateAndSetStatus(ProjectStatus next) {
        Objects.requireNonNull(next, "Target status cannot be null");
        if (!this.status.canTransitionTo(next)) {
            throw new InvalidProjectStateException(this.status, next);
        }
        this.status = next;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Calculates realistic mathematical project completion percentage.
     * If milestones exist, progress is 60% milestones + 40% task completion.
     * Otherwise progress is purely percentage of tasks completed.
     */
    public double calculateProgressPercentage() {
        double milestoneProgress = 0.0;
        if (milestones != null && !milestones.isEmpty()) {
            double totalWeight = milestones.stream().mapToDouble(Milestone::getWeightPercentage).sum();
            double completedWeight = milestones.stream()
                    .filter(Milestone::isCompleted)
                    .mapToDouble(Milestone::getWeightPercentage)
                    .sum();
            milestoneProgress = (totalWeight > 0) ? (completedWeight / totalWeight) * 100.0 : 0.0;
        }

        double taskProgress = 0.0;
        if (tasks != null && !tasks.isEmpty()) {
            long completedCount = tasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
            taskProgress = ((double) completedCount / tasks.size()) * 100.0;
        }

        if (milestones != null && !milestones.isEmpty() && tasks != null && !tasks.isEmpty()) {
            return Math.round((0.6 * milestoneProgress + 0.4 * taskProgress) * 10.0) / 10.0;
        } else if (milestones != null && !milestones.isEmpty()) {
            return Math.round(milestoneProgress * 10.0) / 10.0;
        } else if (tasks != null && !tasks.isEmpty()) {
            return Math.round(taskProgress * 10.0) / 10.0;
        }
        return 0.0;
    }

    public boolean isOverdue() {
        if (status == ProjectStatus.COMPLETED || status == ProjectStatus.ARCHIVED) return false;
        return deadline != null && deadline.isBefore(LocalDate.now());
    }

    public void addRequiredSkill(Skill skill, ProficiencyLevel minLevel, double weight) {
        ProjectSkill ps = new ProjectSkill(this, skill, minLevel, weight);
        this.requiredSkills.remove(ps);
        this.requiredSkills.add(ps);
    }

    public void logActivity(User user, String actionType, String description) {
        ActivityLog log = new ActivityLog(this, user, actionType, description);
        this.activityLogs.add(log);
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

    public Student getOwner() {
        return owner;
    }

    public void setOwner(Student owner) {
        this.owner = owner;
    }

    public Faculty getMentor() {
        return mentor;
    }

    public void setMentor(Faculty mentor) {
        this.mentor = mentor;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public int getTeamSizeLimit() {
        return teamSizeLimit;
    }

    public void setTeamSizeLimit(int teamSizeLimit) {
        this.teamSizeLimit = teamSizeLimit;
    }

    public String getTechStack() {
        return techStack;
    }

    public void setTechStack(String techStack) {
        this.techStack = techStack;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Set<ProjectSkill> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(Set<ProjectSkill> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Milestone> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<Milestone> milestones) {
        this.milestones = milestones;
    }

    public List<ActivityLog> getActivityLogs() {
        return activityLogs;
    }

    public void setActivityLogs(List<ActivityLog> activityLogs) {
        this.activityLogs = activityLogs;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
