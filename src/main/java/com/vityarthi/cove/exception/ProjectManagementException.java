package com.vityarthi.cove.exception;

/**
 * Base custom application exception.
 * Demonstrates:
 * - CSE2006 Unit 3: Exception hierarchy, inheritance in exception handling, super constructor chaining.
 */
public class ProjectManagementException extends RuntimeException {

    private final String errorCode;

    public ProjectManagementException(String message) {
        super(message);
        this.errorCode = "GENERIC_ERROR";
    }

    public ProjectManagementException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ProjectManagementException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
