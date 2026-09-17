package com.vityarthi.cove.model;

/**
 * Skill proficiency levels with numerical weights.
 * Demonstrates:
 * - CSE2006 Unit 2: Enum with fields, constructor, methods, and comparative logic.
 */
public enum ProficiencyLevel {
    BEGINNER(1, 0.25, "Basic conceptual familiarity and introductory exercises"),
    INTERMEDIATE(2, 0.50, "Practical project experience and independent implementation"),
    ADVANCED(3, 0.75, "High proficiency, complex problem-solving, and optimization"),
    EXPERT(4, 1.00, "Deep architectural mastery, debugging complex systems, and mentoring others");

    private final int levelRank;
    private final double scoreWeight;
    private final String description;

    ProficiencyLevel(int levelRank, double scoreWeight, String description) {
        this.levelRank = levelRank;
        this.scoreWeight = scoreWeight;
        this.description = description;
    }

    public int getLevelRank() {
        return levelRank;
    }

    public double getScoreWeight() {
        return scoreWeight;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Checks if this level satisfies or exceeds the required threshold.
     */
    public boolean satisfies(ProficiencyLevel required) {
        if (required == null) return true;
        return this.levelRank >= required.levelRank;
    }
}
