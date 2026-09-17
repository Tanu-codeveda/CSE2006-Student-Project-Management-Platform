package com.vityarthi.cove.model;

import java.util.EnumSet;
import java.util.Set;

/**
 * Project lifecycle states with strict state transition validation.
 * Demonstrates:
 * - CSE2006 Unit 1 & 2: Enums with business logic and set-based validation rules.
 */
public enum ProjectStatus {
    IDEA("Initial Proposal / Concept"),
    OPEN("Open for Member Inquiries"),
    TEAM_FORMING("Actively Reviewing Candidates & Forming Team"),
    IN_PROGRESS("Active Development & Task Execution"),
    COMPLETED("Evaluation Finished / All Deliverables Met"),
    ARCHIVED("Historical Record / Read-Only");

    private final String displayLabel;

    ProjectStatus(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    /**
     * Validates whether transition from 'this' to 'next' is logically permitted.
     * Prevents invalid state jumps (e.g., IDEA directly to COMPLETED, or reviving ARCHIVED).
     */
    public boolean canTransitionTo(ProjectStatus next) {
        if (next == null) return false;
        if (this == next) return true; // Idempotent

        switch (this) {
            case IDEA:
                return next == OPEN || next == TEAM_FORMING || next == ARCHIVED;
            case OPEN:
                return next == TEAM_FORMING || next == IN_PROGRESS || next == ARCHIVED;
            case TEAM_FORMING:
                return next == IN_PROGRESS || next == OPEN || next == ARCHIVED;
            case IN_PROGRESS:
                return next == COMPLETED || next == ARCHIVED;
            case COMPLETED:
                return next == ARCHIVED;
            case ARCHIVED:
                return false; // Terminal state
            default:
                return false;
        }
    }

    public Set<ProjectStatus> getValidNextStates() {
        Set<ProjectStatus> valid = EnumSet.noneOf(ProjectStatus.class);
        for (ProjectStatus status : ProjectStatus.values()) {
            if (this.canTransitionTo(status) && status != this) {
                valid.add(status);
            }
        }
        return valid;
    }
}
