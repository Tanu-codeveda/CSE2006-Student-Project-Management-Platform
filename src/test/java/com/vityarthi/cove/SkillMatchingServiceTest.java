package com.vityarthi.cove;

import com.vityarthi.cove.dto.SkillMatchResponse;
import com.vityarthi.cove.model.*;
import com.vityarthi.cove.repository.ProjectRepository;
import com.vityarthi.cove.repository.StudentRepository;
import com.vityarthi.cove.service.SkillMatchingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests verifying the Rule-Based Skill Compatibility Matching Engine.
 * Demonstrates:
 * - Method overloading validation.
 * - Deterministic scoring and explainability assertions.
 */
@ExtendWith(MockitoExtension.class)
public class SkillMatchingServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private SkillMatchingService skillMatchingService;

    private Student student;
    private Project project;
    private Skill javaSkill;
    private Skill sqlSkill;
    private Skill reactSkill;

    @BeforeEach
    void setUp() {
        Student owner = new Student("Project Lead", "lead@vitbhopal.ac.in", "pass", "21BCE0001", "CSE", 3);
        owner.setId(1L);

        project = new Project("Distributed Cache", "In-memory distributed key-value store", owner, 4, LocalDate.now().plusMonths(2));
        project.setId(100L);

        javaSkill = new Skill(1L, "Java", SkillCategory.PROGRAMMING, "Java SE");
        sqlSkill = new Skill(2L, "SQL", SkillCategory.DATABASE, "SQL");
        reactSkill = new Skill(3L, "React", SkillCategory.WEB_DEVELOPMENT, "React JS");

        project.addRequiredSkill(javaSkill, ProficiencyLevel.INTERMEDIATE, 1.5);
        project.addRequiredSkill(sqlSkill, ProficiencyLevel.INTERMEDIATE, 1.0);

        student = new Student("Candidate Alice", "alice@vitbhopal.ac.in", "pass", "21BCE1050", "CSE", 3);
        student.setId(2L);
    }

    @Test
    @DisplayName("Should achieve 100% match when all required skills are mastered")
    void testPerfectSkillMatch() {
        student.addSkill(javaSkill, ProficiencyLevel.ADVANCED, 2.0);
        student.addSkill(sqlSkill, ProficiencyLevel.INTERMEDIATE, 1.5);

        SkillMatchResponse result = skillMatchingService.evaluateStudent(student, project);

        assertNotNull(result);
        assertEquals(2, result.getMatchedSkillCount());
        assertEquals(2, result.getTotalRequiredSkillCount());
        assertEquals(100.0, result.getMatchPercentage());
        assertEquals("PERFECT_MATCH", result.getCompatibilityTier());
        assertEquals(2, result.getSkillBreakdown().size());
        assertTrue(result.getSkillBreakdown().stream().allMatch(b -> b.getStatusBadge().contains("MATCH")));
    }

    @Test
    @DisplayName("Should detect partial match when student has lower proficiency than required")
    void testPartialProficiencyMatch() {
        student.addSkill(javaSkill, ProficiencyLevel.BEGINNER, 0.5); // Required INTERMEDIATE
        student.addSkill(sqlSkill, ProficiencyLevel.INTERMEDIATE, 1.0);

        SkillMatchResponse result = skillMatchingService.evaluateStudent(student, project, false);

        assertEquals(2, result.getMatchedSkillCount());
        assertEquals(100.0, result.getMatchPercentage());
        assertTrue(result.getWeightedScore() < 1.0); // Penalty for lower proficiency
        assertTrue(result.getSkillBreakdown().stream().anyMatch(b -> b.getStatusBadge().contains("PARTIAL")));
    }

    @Test
    @DisplayName("Should calculate 0% match when student possesses completely disjoint skills")
    void testZeroSkillMatch() {
        student.addSkill(reactSkill, ProficiencyLevel.EXPERT, 3.0); // Not required

        SkillMatchResponse result = skillMatchingService.evaluateStudent(student, project);

        assertEquals(0, result.getMatchedSkillCount());
        assertEquals(2, result.getTotalRequiredSkillCount());
        assertEquals(0.0, result.getMatchPercentage());
        assertEquals("LOW_MATCH", result.getCompatibilityTier());
        assertTrue(result.getSkillBreakdown().stream().allMatch(b -> b.getStatusBadge().contains("MISSING")));
    }
}
