package com.vityarthi.cove.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Objects;

/**
 * Required skill specification for an academic project.
 * Demonstrates:
 * - CSE2006 Unit 2: Encapsulation.
 * - CSE2006 Unit 5: JPA Relational Mappings.
 */
@Entity
@Table(name = "project_skills", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"project_id", "skill_id"})
})
public class ProjectSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(name = "min_proficiency", nullable = false, length = 20)
    private ProficiencyLevel minProficiency = ProficiencyLevel.BEGINNER;

    @Column(nullable = false)
    private double weight = 1.0; // Skill relative importance weight

    public ProjectSkill() {}

    public ProjectSkill(Project project, Skill skill, ProficiencyLevel minProficiency, double weight) {
        this.project = Objects.requireNonNull(project, "Project must not be null");
        this.skill = Objects.requireNonNull(skill, "Skill must not be null");
        this.minProficiency = minProficiency != null ? minProficiency : ProficiencyLevel.BEGINNER;
        this.weight = weight > 0 ? weight : 1.0;
    }

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

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public ProficiencyLevel getMinProficiency() {
        return minProficiency;
    }

    public void setMinProficiency(ProficiencyLevel minProficiency) {
        this.minProficiency = minProficiency;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectSkill that = (ProjectSkill) o;
        return Objects.equals(skill, that.skill);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skill);
    }
}
