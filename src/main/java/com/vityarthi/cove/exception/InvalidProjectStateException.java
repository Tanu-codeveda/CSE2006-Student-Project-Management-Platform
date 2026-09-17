package com.vityarthi.cove.exception;

import com.vityarthi.cove.model.ProjectStatus;

/**
 * Thrown when an illegal project lifecycle transition is attempted.
 */
public class InvalidProjectStateException extends ProjectManagementException {

    public InvalidProjectStateException(ProjectStatus current, ProjectStatus target) {
        super("INVALID_PROJECT_STATE", "Illegal project state transition from " + current + " to " + target + ".");
    }

    public InvalidProjectStateException(String message) {
        super("INVALID_PROJECT_STATE", message);
    }
}
