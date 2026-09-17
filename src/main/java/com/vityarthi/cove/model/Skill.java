package com.vityarthi.cove.model;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Extensible Skill Entity.
 * Demonstrates:
 * - CSE2006 Unit 2: Encapsulation, Constructor overloading.
 * - CSE2006 Unit 5: JPA Entity.
 */
@Entity
@Table(name = "skills")
public class Skill implements Comparable<Skill> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SkillCategory category;

    @Column(length = 255)
    private String description;

    public Skill() {}

    public Skill(String name, SkillCategory category, String description) {
        this.name = Objects.requireNonNull(name, "Skill name is mandatory").trim();
        this.category = Objects.requireNonNull(category, "Skill category is mandatory");
        this.description = description;
    }

    public Skill(Long id, String name, SkillCategory category, String description) {
        this(name, category, description);
        this.id = id;
    }

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

    public SkillCategory getCategory() {
        return category;
    }

    public void setCategory(SkillCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int compareTo(Skill other) {
        if (other == null) return 1;
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skill skill = (Skill) o;
        return name.equalsIgnoreCase(skill.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return "Skill{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                '}';
    }
}
