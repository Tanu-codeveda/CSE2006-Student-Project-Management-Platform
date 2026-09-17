package com.vityarthi.cove.exception;

import com.vityarthi.cove.model.TaskStatus;

/**
 * Thrown when an illegal task status transition is attempted.
 */
public class InvalidTaskStateException extends ProjectManagementException {

    public InvalidTaskStateException(TaskStatus current, TaskStatus target) {
        super("INVALID_TASK_STATE", "Illegal task status transition from " + current + " to " + target + ".");
    }

    public InvalidTaskStateException(String message) {
        super("INVALID_TASK_STATE", message);
    }
}
