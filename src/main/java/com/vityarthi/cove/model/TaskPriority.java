package com.vityarthi.cove.model;

/**
 * Task priority levels.
 * Demonstrates:
 * - CSE2006 Unit 2: Enum with priority weighting for scheduling queues.
 */
public enum TaskPriority {
    LOW(1, "Low Priority / Enhancement"),
    MEDIUM(2, "Standard Priority"),
    HIGH(3, "High Priority / Milestone Driver"),
    CRITICAL(4, "Critical Path / Blocker");

    private final int severity;
    private final String description;

    TaskPriority(int severity, String description) {
        this.severity = severity;
        this.description = description;
    }

    public int getSeverity() {
        return severity;
    }

    public String getDescription() {
        return description;
    }
}
