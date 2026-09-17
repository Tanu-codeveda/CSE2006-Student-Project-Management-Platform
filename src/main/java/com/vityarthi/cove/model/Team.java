package com.vityarthi.cove.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vityarthi.cove.exception.TeamFullException;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Team entity managing member allocation and concurrency-safe slot claims.
 * Demonstrates:
 * - CSE2006 Unit 2: Encapsulation and Collections (List<TeamMember>).
 * - CSE2006 Unit 3: Java Synchronization (synchronized method on critical section preventing race conditions).
 * - CSE2006 Unit 5: JPA OneToOne and OneToMany relationships, @Version for optimistic locking.
 */
@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    private Project project;

    @Column(name = "max_members", nullable = false)
    private int maxMembers = 4;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TeamMember> members = new ArrayList<>();

    @Version
    private Long version;

    public Team() {}

    public Team(Project project, int maxMembers) {
        this.project = Objects.requireNonNull(project, "Project is mandatory for a team");
        this.maxMembers = Math.max(1, maxMembers);
    }

    /**
     * Synchronized method providing thread-safe slot allocation.
     * Prevents race condition when multiple concurrent users attempt to claim the final team slot.
     * Demonstrates CSE2006 Unit 3: Synchronization.
     */
    public synchronized boolean addMemberSynchronized(Student student, String roleInTeam) {
        Objects.requireNonNull(student, "Student cannot be null");

        // Check if student is already in the team
        if (hasStudent(student)) {
            throw new IllegalArgumentException("Student " + student.getName() + " (" + student.getRegistrationNumber() + ") is already a member of this team.");
        }

        // Critical Section check: atomic capacity enforcement
        if (this.members.size() >= this.maxMembers) {
            throw new TeamFullException(this.members.size(), this.maxMembers);
        }

        TeamMember member = new TeamMember(this, student, roleInTeam);
        this.members.add(member);
        return true;
    }

    public synchronized boolean removeMemberSynchronized(Student student) {
        Objects.requireNonNull(student, "Student cannot be null");
        return this.members.removeIf(m -> m.getStudent().equals(student));
    }

    public boolean hasStudent(Student student) {
        if (student == null) return false;
        return members.stream().anyMatch(m -> m.getStudent().equals(student));
    }

    public boolean isFull() {
        return members.size() >= maxMembers;
    }

    public int getAvailableSlots() {
        return Math.max(0, maxMembers - members.size());
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public List<TeamMember> getMembers() {
        return members;
    }

    public void setMembers(List<TeamMember> members) {
        this.members = members;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
