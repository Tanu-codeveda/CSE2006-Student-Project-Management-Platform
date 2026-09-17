package com.vityarthi.cove.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Transparent, explainable evaluation result for student-project skill compatibility.
 * Strictly rule-based, deterministic algorithm (CSE2006 syllabus demonstration).
 */
public class SkillMatchResponse implements Comparable<SkillMatchResponse> {

    private Long studentId;
    private String studentName;
    private String registrationNumber;
    private String department;
    private int yearOfStudy;
    private int matchedSkillCount;
    private int totalRequiredSkillCount;
    private double matchPercentage; // e.g. 100.0, 66.7
    private double weightedScore;
    private String compatibilityTier; // PERFECT_MATCH, STRONG_MATCH, PARTIAL_MATCH, LOW_MATCH
    private String explanation;
    private List<SkillItemEvaluation> skillBreakdown = new ArrayList<>();

    public static class SkillItemEvaluation {
        private String skillName;
        private String requiredProficiency;
        private String studentProficiency;
        private boolean matched;
        private boolean proficiencySatisfied;
        private String statusBadge; // "MATCH ✓", "PARTIAL ⚠", "MISSING ✗"
        private String reason;

        public SkillItemEvaluation() {}

        public SkillItemEvaluation(String skillName, String requiredProficiency, String studentProficiency,
                                   boolean matched, boolean proficiencySatisfied, String statusBadge, String reason) {
            this.skillName = skillName;
            this.requiredProficiency = requiredProficiency;
            this.studentProficiency = studentProficiency;
            this.matched = matched;
            this.proficiencySatisfied = proficiencySatisfied;
            this.statusBadge = statusBadge;
            this.reason = reason;
        }

        public String getSkillName() {
            return skillName;
        }

        public String getRequiredProficiency() {
            return requiredProficiency;
        }

        public String getStudentProficiency() {
            return studentProficiency;
        }

        public boolean isMatched() {
            return matched;
        }

        public boolean isProficiencySatisfied() {
            return proficiencySatisfied;
        }

        public String getStatusBadge() {
            return statusBadge;
        }

        public String getReason() {
            return reason;
        }
    }

    public SkillMatchResponse() {}

    public SkillMatchResponse(Long studentId, String studentName, String registrationNumber,
                              String department, int yearOfStudy, int matchedSkillCount,
                              int totalRequiredSkillCount, double matchPercentage,
                              double weightedScore, String compatibilityTier, String explanation) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.registrationNumber = registrationNumber;
        this.department = department;
        this.yearOfStudy = yearOfStudy;
        this.matchedSkillCount = matchedSkillCount;
        this.totalRequiredSkillCount = totalRequiredSkillCount;
        this.matchPercentage = matchPercentage;
        this.weightedScore = weightedScore;
        this.compatibilityTier = compatibilityTier;
        this.explanation = explanation;
    }

    @Override
    public int compareTo(SkillMatchResponse other) {
        if (other == null) return -1;
        // Higher weighted score first, then higher raw match percentage
        int cmp = Double.compare(other.weightedScore, this.weightedScore);
        if (cmp != 0) return cmp;
        return Double.compare(other.matchPercentage, this.matchPercentage);
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getDepartment() {
        return department;
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public int getMatchedSkillCount() {
        return matchedSkillCount;
    }

    public int getTotalRequiredSkillCount() {
        return totalRequiredSkillCount;
    }

    public double getMatchPercentage() {
        return matchPercentage;
    }

    public double getWeightedScore() {
        return weightedScore;
    }

    public String getCompatibilityTier() {
        return compatibilityTier;
    }

    public String getExplanation() {
        return explanation;
    }

    public List<SkillItemEvaluation> getSkillBreakdown() {
        return skillBreakdown;
    }

    public void setSkillBreakdown(List<SkillItemEvaluation> skillBreakdown) {
        this.skillBreakdown = skillBreakdown;
    }
}
