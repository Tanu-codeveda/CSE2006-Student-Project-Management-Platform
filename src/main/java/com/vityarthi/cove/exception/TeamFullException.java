package com.vityarthi.cove.exception;

/**
 * Thrown when attempting to add a member to a team that has already reached its maximum capacity.
 * Used to demonstrate race condition prevention during concurrent join operations (CSE2006 Unit 3).
 */
public class TeamFullException extends ProjectManagementException {

    private final int currentSize;
    private final int capacity;

    public TeamFullException(int currentSize, int capacity) {
        super("TEAM_FULL", "Cannot join team: Team capacity reached (" + currentSize + "/" + capacity + " members).");
        this.currentSize = currentSize;
        this.capacity = capacity;
    }

    public int getCurrentSize() {
        return currentSize;
    }

    public int getCapacity() {
        return capacity;
    }
}
