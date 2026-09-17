package com.vityarthi.cove.dto;

import com.vityarthi.cove.model.ProficiencyLevel;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProjectRequest {

    @NotBlank(message = "Project title is required")
    private String title;

    @NotBlank(message = "Project description is required")
    private String description;

    @Min(value = 1, message = "Team size limit must be at least 1")
    private int teamSizeLimit = 4;

    private String techStack;

    @NotNull(message = "Deadline is required")
    @Future(message = "Deadline must be in the future")
    private LocalDate deadline;

    private List<SkillRequirement> requiredSkills = new ArrayList<>();

    public static class SkillRequirement {
        private String skillName;
        private ProficiencyLevel minProficiency = ProficiencyLevel.BEGINNER;
        private double weight = 1.0;

        public SkillRequirement() {}

        public SkillRequirement(String skillName, ProficiencyLevel minProficiency, double weight) {
            this.skillName = skillName;
            this.minProficiency = minProficiency;
            this.weight = weight;
        }

        public String getSkillName() {
            return skillName;
        }

        public void setSkillName(String skillName) {
            this.skillName = skillName;
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
    }

    public ProjectRequest() {}

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

    public List<SkillRequirement> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<SkillRequirement> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
}
