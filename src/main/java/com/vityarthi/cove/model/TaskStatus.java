package com.vityarthi.cove.model;

/**
 * Task lifecycle status states with transition rules.
 */
public enum TaskStatus {
    TODO("Pending Assignment / Backlog"),
    IN_PROGRESS("Currently in Active Development"),
    BLOCKED("Impeded by Dependencies or External Blockers"),
    COMPLETED("Successfully Tested and Merged");

    private final String label;

    TaskStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean canTransitionTo(TaskStatus next) {
        if (next == null) return false;
        if (this == next) return true;

        switch (this) {
            case TODO:
                return next == IN_PROGRESS || next == BLOCKED;
            case IN_PROGRESS:
                return next == TODO || next == BLOCKED || next == COMPLETED;
            case BLOCKED:
                return next == TODO || next == IN_PROGRESS;
            case COMPLETED:
                return next == IN_PROGRESS; // Re-open if regression discovered
            default:
                return false;
        }
    }
}
