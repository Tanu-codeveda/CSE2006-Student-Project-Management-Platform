package com.vityarthi.cove.model;

/**
 * User roles in the platform.
 * Demonstrates Java Enum concept from CSE2006 Unit 2.
 */
public enum Role {
    STUDENT("Student Learner / Contributor"),
    FACULTY("Project Mentor / Faculty Guide"),
    ADMINISTRATOR("System Administrator");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
