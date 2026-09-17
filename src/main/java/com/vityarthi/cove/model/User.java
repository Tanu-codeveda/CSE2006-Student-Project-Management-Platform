package com.vityarthi.cove.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Base Abstract Class representing any authenticated user in the system.
 * Demonstrates:
 * - CSE2006 Unit 2: Abstract classes, inheritance, encapsulation, constructor overloading, constructor chaining.
 * - CSE2006 Unit 5: JPA Entity inheritance with JOINED table strategy.
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean active = true;

    // Default Constructor (Required by JPA)
    protected User() {
        this.createdAt = LocalDateTime.now();
    }

    // Overloaded Constructor 1: Common baseline constructor
    protected User(String name, String email, String password, Role role) {
        this();
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.password = Objects.requireNonNull(password, "Password cannot be null");
        this.role = Objects.requireNonNull(role, "Role cannot be null");
    }

    // Overloaded Constructor 2: Complete constructor chaining
    protected User(Long id, String name, String email, String password, Role role, boolean active) {
        this(name, email, password, role);
        this.id = id;
        this.active = active;
    }

    // --- Abstract Methods demonstrating Polymorphism (CSE2006 Unit 2) ---

    /**
     * Returns the human-readable role name.
     */
    public abstract String getRoleName();

    /**
     * Returns the front-end dashboard route appropriate for this role.
     */
    public abstract String getDashboardUrl();

    /**
     * Checks if this user type is authorized to author projects.
     */
    public abstract boolean canCreateProjects();

    /**
     * Checks if this user type is authorized to allocate project tasks.
     */
    public abstract boolean canAssignTasks();

    // --- Concrete Encapsulated Getters & Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    protected void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) || Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", active=" + active +
                '}';
    }
}
