package com.vityarthi.cove.model;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Concrete Faculty Mentor entity extending User.
 * Demonstrates:
 * - CSE2006 Unit 2: Inheritance, method overriding, super keyword.
 */
@Entity
@Table(name = "faculty")
@PrimaryKeyJoinColumn(name = "user_id")
public class Faculty extends User {

    @Column(nullable = false, unique = true, length = 30)
    private String employeeId;

    @Column(nullable = false, length = 80)
    private String department;

    @Column(nullable = false, length = 60)
    private String designation;

    @Column(length = 30)
    private String officeRoom;

    public Faculty() {
        super();
        setRole(Role.FACULTY);
    }

    public Faculty(String name, String email, String password, String employeeId, String department, String designation) {
        super(name, email, password, Role.FACULTY);
        this.employeeId = Objects.requireNonNull(employeeId, "Employee ID is mandatory").toUpperCase();
        this.department = Objects.requireNonNull(department, "Department is mandatory");
        this.designation = Objects.requireNonNull(designation, "Designation is mandatory");
    }

    @Override
    public String getRoleName() {
        return "Faculty Mentor (" + designation + ", " + department + ")";
    }

    @Override
    public String getDashboardUrl() {
        return "/faculty-dashboard.html";
    }

    @Override
    public boolean canCreateProjects() {
        return false; // Students propose/author academic projects; faculty mentor/evaluate
    }

    @Override
    public boolean canAssignTasks() {
        return true; // Faculty mentors can guide task allocation
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getOfficeRoom() {
        return officeRoom;
    }

    public void setOfficeRoom(String officeRoom) {
        this.officeRoom = officeRoom;
    }
}
