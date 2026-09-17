package com.vityarthi.cove.exception;

/**
 * Thrown when a user attempts an action not permitted by their role or ownership privileges.
 */
public class UnauthorizedActionException extends ProjectManagementException {

    public UnauthorizedActionException(String message) {
        super("UNAUTHORIZED_ACTION", message);
    }
}
