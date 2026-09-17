package com.vityarthi.cove.model;

import jakarta.persistence.*;

/**
 * Concrete Administrator entity extending User.
 * Demonstrates:
 * - CSE2006 Unit 2: Inheritance and polymorphism.
 */
@Entity
@Table(name = "administrators")
@PrimaryKeyJoinColumn(name = "user_id")
public class Administrator extends User {

    @Column(nullable = false, unique = true, length = 30)
    private String adminCode;

    @Column(name = "super_admin")
    private boolean superAdmin = false;

    public Administrator() {
        super();
        setRole(Role.ADMINISTRATOR);
    }

    public Administrator(String name, String email, String password, String adminCode, boolean superAdmin) {
        super(name, email, password, Role.ADMINISTRATOR);
        this.adminCode = adminCode;
        this.superAdmin = superAdmin;
    }

    @Override
    public String getRoleName() {
        return superAdmin ? "Super Administrator" : "Platform Administrator";
    }

    @Override
    public String getDashboardUrl() {
        return "/admin-dashboard.html";
    }

    @Override
    public boolean canCreateProjects() {
        return true;
    }

    @Override
    public boolean canAssignTasks() {
        return true;
    }

    public String getAdminCode() {
        return adminCode;
    }

    public void setAdminCode(String adminCode) {
        this.adminCode = adminCode;
    }

    public boolean isSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        this.superAdmin = superAdmin;
    }
}
