package com.vityarthi.cove.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Objects;

/**
 * Association entity representing a student's acquired skill and proficiency level.
 * Demonstrates:
 * - CSE2006 Unit 2: Encapsulation and constructor overloading.
 * - CSE2006 Unit 5: JPA relational mapping (@ManyToOne).
 */
@Entity
@Table(name = "student_skills", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "skill_id"})
})
public class StudentSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProficiencyLevel proficiency;

    @Column(name = "years_experience")
    private double yearsExperience;

    public StudentSkill() {}

    public StudentSkill(Student student, Skill skill, ProficiencyLevel proficiency) {
        this(student, skill, proficiency, 1.0);
    }

    public StudentSkill(Student student, Skill skill, ProficiencyLevel proficiency, double yearsExperience) {
        this.student = Objects.requireNonNull(student, "Student must not be null");
        this.skill = Objects.requireNonNull(skill, "Skill must not be null");
        this.proficiency = Objects.requireNonNull(proficiency, "Proficiency must not be null");
        this.yearsExperience = yearsExperience;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public ProficiencyLevel getProficiency() {
        return proficiency;
    }

    public void setProficiency(ProficiencyLevel proficiency) {
        this.proficiency = proficiency;
    }

    public double getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(double yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentSkill that = (StudentSkill) o;
        return Objects.equals(skill, that.skill);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skill);
    }
}
