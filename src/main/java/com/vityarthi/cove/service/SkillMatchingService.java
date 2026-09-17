package com.vityarthi.cove.service;

import com.vityarthi.cove.dto.SkillMatchResponse;
import com.vityarthi.cove.exception.ProjectNotFoundException;
import com.vityarthi.cove.model.*;
import com.vityarthi.cove.repository.ProjectRepository;
import com.vityarthi.cove.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Deterministic, Rule-Based Skill Matching Engine.
 * Demonstrates:
 * - CSE2006 Unit 1: Java flow control (if-else, switch, for-each, break).
 * - CSE2006 Unit 2: Method Overloading (compile-time polymorphism).
 * - CSE2006 Unit 4: Java Collections Framework (Collections.sort, Sets, Maps, PriorityQueues).
 * - Transparent explainability without artificial intelligence or black-box ML claims.
 */
@Service
public class SkillMatchingService {

    private final ProjectRepository projectRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public SkillMatchingService(ProjectRepository projectRepository, StudentRepository studentRepository) {
        this.projectRepository = projectRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * Primary matching method: Evaluates all available students against a project's required skills.
     */
    @Transactional(readOnly = true)
    public List<SkillMatchResponse> findMatchingCandidates(Long projectId) {
        return findMatchingCandidates(projectId, 0.0); // Default threshold = 0.0%
    }

    /**
     * Overloaded method 1: Allows filtering by minimum threshold (Method Overloading - CSE2006 Unit 2).
     */
    @Transactional(readOnly = true)
    public List<SkillMatchResponse> findMatchingCandidates(Long projectId, double minThresholdPercentage) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        List<Student> allStudents = studentRepository.findAllActiveWithSkills();
        List<SkillMatchResponse> candidateRankings = new ArrayList<>();

        for (Student candidate : allStudents) {
            // Exclude current project owner and existing team members
            if (candidate.getId().equals(project.getOwner().getId())) {
                continue;
            }
            if (project.getTeam() != null && project.getTeam().hasStudent(candidate)) {
                continue;
            }

            SkillMatchResponse evaluation = evaluateStudent(candidate, project, false);
            if (evaluation.getMatchPercentage() >= minThresholdPercentage) {
                candidateRankings.add(evaluation);
            }
        }

        // Sort using Collections Framework (natural ordering via Comparable interface)
        Collections.sort(candidateRankings);
        return candidateRankings;
    }

    /**
     * Overloaded method 2: Single candidate evaluation against a project (default lenient mode).
     */
    public SkillMatchResponse evaluateStudent(Student student, Project project) {
        return evaluateStudent(student, project, false);
    }

    /**
     * Overloaded method 3: Single candidate evaluation with strict proficiency enforcement.
     */
    public SkillMatchResponse evaluateStudent(Student student, Project project, boolean strictProficiency) {
        Set<ProjectSkill> requiredSkills = project.getRequiredSkills();
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return new SkillMatchResponse(
                    student.getId(), student.getName(), student.getRegistrationNumber(),
                    student.getDepartment(), student.getYearOfStudy(), 0, 0,
                    100.0, 1.0, "PERFECT_MATCH",
                    "No specific skills are required for this project; student is eligible to join."
            );
        }

        int totalRequired = requiredSkills.size();
        int matchedCount = 0;
        double totalWeightedScore = 0.0;
        double maxPossibleWeight = 0.0;

        List<SkillMatchResponse.SkillItemEvaluation> breakdown = new ArrayList<>();

        for (ProjectSkill req : requiredSkills) {
            String skillName = req.getSkill().getName();
            ProficiencyLevel requiredLevel = req.getMinProficiency();
            double skillWeight = req.getWeight();
            maxPossibleWeight += skillWeight;

            // Search student's acquired skills
            ProficiencyLevel studentLevel = student.getProficiencyFor(skillName);

            if (studentLevel != null) {
                boolean proficiencyMet = studentLevel.satisfies(requiredLevel);
                if (proficiencyMet) {
                    matchedCount++;
                    // Base match weight + proficiency bonus
                    double score = skillWeight * (1.0 + 0.1 * (studentLevel.getLevelRank() - requiredLevel.getLevelRank()));
                    totalWeightedScore += score;
                    breakdown.add(new SkillMatchResponse.SkillItemEvaluation(
                            skillName, requiredLevel.name(), studentLevel.name(),
                            true, true, "MATCH ✓",
                            "Meets required " + requiredLevel.name() + " (Actual: " + studentLevel.name() + ")"
                    ));
                } else if (!strictProficiency) {
                    // Partial match: has skill but lower proficiency
                    matchedCount++;
                    double score = skillWeight * 0.6;
                    totalWeightedScore += score;
                    breakdown.add(new SkillMatchResponse.SkillItemEvaluation(
                            skillName, requiredLevel.name(), studentLevel.name(),
                            true, false, "PARTIAL ⚠",
                            "Skill present but below required level (Required: " + requiredLevel.name() + ", Actual: " + studentLevel.name() + ")"
                    ));
                } else {
                    breakdown.add(new SkillMatchResponse.SkillItemEvaluation(
                            skillName, requiredLevel.name(), studentLevel.name(),
                            false, false, "INSUFFICIENT ✗",
                            "Proficiency below mandatory threshold (Required: " + requiredLevel.name() + ")"
                    ));
                }
            } else {
                breakdown.add(new SkillMatchResponse.SkillItemEvaluation(
                            skillName, requiredLevel.name(), "NONE",
                            false, false, "MISSING ✗",
                            "Student does not have '" + skillName + "' registered in profile"
                    ));
            }
        }

        double matchPercentage = totalRequired > 0 ? ((double) matchedCount / totalRequired) * 100.0 : 0.0;
        double normalizedWeightedScore = maxPossibleWeight > 0 ? (totalWeightedScore / maxPossibleWeight) : 0.0;
        matchPercentage = Math.round(matchPercentage * 10.0) / 10.0;
        normalizedWeightedScore = Math.round(normalizedWeightedScore * 1000.0) / 1000.0;

        String tier;
        if (matchedCount == totalRequired && normalizedWeightedScore >= 1.0) {
            tier = "PERFECT_MATCH";
        } else if (matchPercentage >= 70.0) {
            tier = "STRONG_MATCH";
        } else if (matchPercentage >= 40.0) {
            tier = "PARTIAL_MATCH";
        } else {
            tier = "LOW_MATCH";
        }

        String explanation = String.format("Matched %d out of %d required skills (Score: %.1f%%). Compatibility: %s.",
                matchedCount, totalRequired, matchPercentage, tier);

        SkillMatchResponse response = new SkillMatchResponse(
                student.getId(), student.getName(), student.getRegistrationNumber(),
                student.getDepartment(), student.getYearOfStudy(), matchedCount,
                totalRequired, matchPercentage, normalizedWeightedScore, tier, explanation
        );
        response.setSkillBreakdown(breakdown);
        return response;
    }
}
