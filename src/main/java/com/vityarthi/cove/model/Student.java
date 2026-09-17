package com.vityarthi.cove.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Concrete Student entity extending User.
 * Demonstrates:
 * - CSE2006 Unit 2: Inheritance, method overriding, super keyword constructor/method invocation, encapsulation.
 * - CSE2006 Unit 4: Java Collections Framework (Set<StudentSkill>, HashSet).
 * - CSE2006 Unit 5: JPA relational mapping (@OneToMany, @PrimaryKeyJoinColumn).
 */
@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "user_id")
public class Student extends User {

    @Column(nullable = false, unique = true, length = 30)
    private String registrationNumber;

    @Column(nullable = false, length = 80)
    private String department;

    @Column(nullable = false)
    private int yearOfStudy = 2;

    @Column(length = 500)
    private String bio;

    @Column(name = "max_active_projects", nullable = false)
    private int maxActiveProjects = 3;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<StudentSkill> skills = new HashSet<>();

    public Student() {
        super();
        setRole(Role.STUDENT);
    }

    // Parameterized constructor invoking superclass constructor (CSE2006 Exp 7)
    public Student(String name, String email, String password, String registrationNumber, String department, int yearOfStudy) {
        super(name, email, password, Role.STUDENT);
        this.registrationNumber = Objects.requireNonNull(registrationNumber, "Registration number cannot be null").toUpperCase();
        this.department = Objects.requireNonNull(department, "Department cannot be null");
        this.yearOfStudy = yearOfStudy;
    }

    // Overloaded constructor including bio
    public Student(String name, String email, String password, String registrationNumber, String department, int yearOfStudy, String bio) {
        this(name, email, password, registrationNumber, department, yearOfStudy);
        this.bio = bio;
    }

    // --- Method Overriding demonstrating Dynamic Dispatch (CSE2006 Exp 6 & 9) ---

    @Override
    public String getRoleName() {
        return "Student Contributor (" + registrationNumber + ")";
    }

    @Override
    public String getDashboardUrl() {
        return "/dashboard.html";
    }

    @Override
    public boolean canCreateProjects() {
        return true;
    }

    @Override
    public boolean canAssignTasks() {
        return true;
    }

    // Helper methods for skill management (Collections)
    public void addSkill(Skill skill, ProficiencyLevel level, double yearsExperience) {
        StudentSkill studentSkill = new StudentSkill(this, skill, level, yearsExperience);
        this.skills.remove(studentSkill);
        this.skills.add(studentSkill);
    }

    public void removeSkill(Skill skill) {
        this.skills.removeIf(s -> s.getSkill().equals(skill));
    }

    public boolean hasSkill(String skillName) {
        return skills.stream().anyMatch(s -> s.getSkill().getName().equalsIgnoreCase(skillName));
    }

    public ProficiencyLevel getProficiencyFor(String skillName) {
        return skills.stream()
                .filter(s -> s.getSkill().getName().equalsIgnoreCase(skillName))
                .map(StudentSkill::getProficiency)
                .findFirst()
                .orElse(null);
    }

    // --- Getters and Setters ---

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getMaxActiveProjects() {
        return maxActiveProjects;
    }

    public void setMaxActiveProjects(int maxActiveProjects) {
        this.maxActiveProjects = maxActiveProjects;
    }

    public Set<StudentSkill> getSkills() {
        return skills;
    }

    public void setSkills(Set<StudentSkill> skills) {
        this.skills = skills;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", department='" + department + '\'' +
                ", skillsCount=" + skills.size() +
                '}';
    }
}
