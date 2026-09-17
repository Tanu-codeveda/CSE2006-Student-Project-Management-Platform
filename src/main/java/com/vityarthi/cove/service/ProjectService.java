package com.vityarthi.cove.service;

import com.vityarthi.cove.dto.ProjectRequest;
import com.vityarthi.cove.exception.ProjectNotFoundException;
import com.vityarthi.cove.exception.SkillNotFoundException;
import com.vityarthi.cove.exception.UnauthorizedActionException;
import com.vityarthi.cove.exception.UserNotFoundException;
import com.vityarthi.cove.model.*;
import com.vityarthi.cove.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service managing project lifecycles, states, and team coordination.
 */
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final SkillRepository skillRepository;
    private final ActivityLogRepository activityLogRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository,
                          StudentRepository studentRepository,
                          FacultyRepository facultyRepository,
                          SkillRepository skillRepository,
                          ActivityLogRepository activityLogRepository) {
        this.projectRepository = projectRepository;
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.skillRepository = skillRepository;
        this.activityLogRepository = activityLogRepository;
    }

    @Transactional
    public Project createProject(Long ownerId, ProjectRequest request) {
        Student owner = studentRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("Student with ID " + ownerId + " not found"));

        if (!owner.canCreateProjects()) {
            throw new UnauthorizedActionException("User role is not authorized to create projects.");
        }

        Project project = new Project(
                request.getTitle(),
                request.getDescription(),
                owner,
                request.getTeamSizeLimit(),
                request.getTechStack(),
                request.getDeadline()
        );

        // Add requested skills
        if (request.getRequiredSkills() != null) {
            for (ProjectRequest.SkillRequirement req : request.getRequiredSkills()) {
                Skill skill = skillRepository.findByNameIgnoreCase(req.getSkillName())
                        .orElseThrow(() -> new SkillNotFoundException(req.getSkillName()));
                project.addRequiredSkill(skill, req.getMinProficiency(), req.getWeight());
            }
        }

        Project saved = projectRepository.save(project);

        ActivityLog log = new ActivityLog(saved, owner, "PROJECT_CREATED",
                "Project '" + saved.getTitle() + "' proposed by " + owner.getName() + ".");
        activityLogRepository.save(log);

        return saved;
    }

    @Transactional
    public Project updateStatus(Long projectId, Long userId, ProjectStatus newStatus) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        // Authorization check: Only project owner, mentor, or admin can change status
        boolean isOwner = project.getOwner().getId().equals(userId);
        boolean isMentor = project.getMentor() != null && project.getMentor().getId().equals(userId);

        if (!isOwner && !isMentor) {
            throw new UnauthorizedActionException("Only project lead or assigned mentor can modify project status.");
        }

        // Validate state machine transition (throws InvalidProjectStateException on failure)
        project.validateAndSetStatus(newStatus);
        Project updated = projectRepository.save(project);

        ActivityLog log = new ActivityLog(project, project.getOwner(), "STATUS_CHANGED",
                "Project status transitioned to " + newStatus.name() + " (" + newStatus.getDisplayLabel() + ").");
        activityLogRepository.save(log);

        return updated;
    }

    @Transactional
    public Project assignMentor(Long projectId, Long facultyId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new UserNotFoundException("Faculty with ID " + facultyId + " not found."));

        project.setMentor(faculty);
        Project saved = projectRepository.save(project);

        ActivityLog log = new ActivityLog(project, faculty, "MENTOR_ASSIGNED",
                "Faculty mentor " + faculty.getName() + " (" + faculty.getDesignation() + ") assigned to project.");
        activityLogRepository.save(log);

        return saved;
    }

    @Transactional(readOnly = true)
    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
    }

    @Transactional(readOnly = true)
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Project> searchProjects(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return projectRepository.findAll();
        }
        return projectRepository.searchProjects(keyword);
    }

    @Transactional(readOnly = true)
    public List<Project> getProjectsByOwner(Long ownerId) {
        return projectRepository.findByOwnerId(ownerId);
    }

    @Transactional(readOnly = true)
    public List<Project> getProjectsByStudentMembership(Long studentId) {
        return projectRepository.findProjectsByStudentMembership(studentId);
    }
}
