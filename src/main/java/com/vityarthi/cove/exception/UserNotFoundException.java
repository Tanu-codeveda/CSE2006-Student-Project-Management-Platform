package com.vityarthi.cove.exception;

/**
 * Thrown when a user (student/faculty/admin) cannot be found.
 */
public class UserNotFoundException extends ProjectManagementException {

    public UserNotFoundException(Long userId) {
        super("USER_NOT_FOUND", "User with ID " + userId + " does not exist.");
    }

    public UserNotFoundException(String identifier) {
        super("USER_NOT_FOUND", "User identifier '" + identifier + "' not found.");
    }
}
