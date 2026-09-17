package com.vityarthi.cove.model;

/**
 * Skill categorization tags for structured discovery.
 */
public enum SkillCategory {
    PROGRAMMING("Core Programming Languages"),
    WEB_DEVELOPMENT("Frontend & Web Technologies"),
    BACKEND("Backend Frameworks & Server Runtimes"),
    DATABASE("Relational & NoSQL Databases"),
    SECURITY("Cybersecurity & Cryptography"),
    CLOUD("Cloud Services & Infrastructure"),
    DEVOPS("DevOps, Containers & CI/CD"),
    AI_ML("Artificial Intelligence & Data Science"),
    DESIGN("UI/UX & Interactive Design"),
    OTHER("General Technical Skill");

    private final String label;

    SkillCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
