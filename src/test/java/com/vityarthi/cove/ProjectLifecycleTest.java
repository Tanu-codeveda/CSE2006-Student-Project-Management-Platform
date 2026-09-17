package com.vityarthi.cove;

import com.vityarthi.cove.exception.InvalidProjectStateException;
import com.vityarthi.cove.model.Project;
import com.vityarthi.cove.model.ProjectStatus;
import com.vityarthi.cove.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests verifying the Project Lifecycle State Machine and validation logic.
 * Demonstrates:
 * - CSE2006 Unit 1 & 3: Flow control, enum state transitions, and custom exception assertions.
 */
public class ProjectLifecycleTest {

    private Project project;

    @BeforeEach
    void setUp() {
        Student owner = new Student("Bob", "bob@vit.ac.in", "pass", "21BCE1200", "CSE", 3);
        project = new Project("IoT Smart Meter", "Smart metering device", owner, 3, LocalDate.now().plusMonths(3));
    }

    @Test
    @DisplayName("Should successfully follow standard lifecycle: IDEA -> OPEN -> TEAM_FORMING -> IN_PROGRESS -> COMPLETED -> ARCHIVED")
    void testValidLifecycleProgression() {
        assertEquals(ProjectStatus.IDEA, project.getStatus());

        project.validateAndSetStatus(ProjectStatus.OPEN);
        assertEquals(ProjectStatus.OPEN, project.getStatus());

        project.validateAndSetStatus(ProjectStatus.TEAM_FORMING);
        assertEquals(ProjectStatus.TEAM_FORMING, project.getStatus());

        project.validateAndSetStatus(ProjectStatus.IN_PROGRESS);
        assertEquals(ProjectStatus.IN_PROGRESS, project.getStatus());

        project.validateAndSetStatus(ProjectStatus.COMPLETED);
        assertEquals(ProjectStatus.COMPLETED, project.getStatus());

        project.validateAndSetStatus(ProjectStatus.ARCHIVED);
        assertEquals(ProjectStatus.ARCHIVED, project.getStatus());
    }

    @Test
    @DisplayName("Should throw InvalidProjectStateException when skipping required intermediate phases")
    void testDisallowedStateJump() {
        assertEquals(ProjectStatus.IDEA, project.getStatus());

        assertThrows(InvalidProjectStateException.class, () -> {
            project.validateAndSetStatus(ProjectStatus.COMPLETED); // Cannot complete an idea directly
        });
    }

    @Test
    @DisplayName("Should forbid reviving an archived project")
    void testArchivedTerminalState() {
        project.validateAndSetStatus(ProjectStatus.ARCHIVED);

        assertThrows(InvalidProjectStateException.class, () -> {
            project.validateAndSetStatus(ProjectStatus.OPEN);
        });
    }
}
