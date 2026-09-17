package com.vityarthi.cove.exception;

/**
 * Thrown when a requested project does not exist.
 */
public class ProjectNotFoundException extends ProjectManagementException {

    public ProjectNotFoundException(Long projectId) {
        super("PROJECT_NOT_FOUND", "Project with ID " + projectId + " was not found.");
    }

    public ProjectNotFoundException(String message) {
        super("PROJECT_NOT_FOUND", message);
    }
}
