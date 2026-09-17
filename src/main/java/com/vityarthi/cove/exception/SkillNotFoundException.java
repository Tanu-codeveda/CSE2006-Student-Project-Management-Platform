package com.vityarthi.cove.exception;

/**
 * Thrown when a specified skill is not present in the catalog.
 */
public class SkillNotFoundException extends ProjectManagementException {

    public SkillNotFoundException(String skillName) {
        super("SKILL_NOT_FOUND", "Skill '" + skillName + "' does not exist in the taxonomy catalog.");
    }

    public SkillNotFoundException(Long id) {
        super("SKILL_NOT_FOUND", "Skill with ID " + id + " does not exist.");
    }
}
