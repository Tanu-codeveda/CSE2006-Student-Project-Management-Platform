package com.vityarthi.cove.exception;

/**
 * Thrown when attempting to assign a skill that is already registered for a student or project.
 */
public class DuplicateSkillException extends ProjectManagementException {

    public DuplicateSkillException(String skillName) {
        super("DUPLICATE_SKILL", "Skill '" + skillName + "' is already assigned.");
    }
}
