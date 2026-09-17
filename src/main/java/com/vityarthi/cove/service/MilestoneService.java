package com.vityarthi.cove.service;

import com.vityarthi.cove.exception.ProjectNotFoundException;
import com.vityarthi.cove.model.ActivityLog;
import com.vityarthi.cove.model.Milestone;
import com.vityarthi.cove.model.Project;
import com.vityarthi.cove.repository.ActivityLogRepository;
import com.vityarthi.cove.repository.MilestoneRepository;
import com.vityarthi.cove.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service managing academic milestone tracking and progress calculations.
 */
@Service
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;
    private final ActivityLogRepository activityLogRepository;

    @Autowired
    public MilestoneService(MilestoneRepository milestoneRepository,
                            ProjectRepository projectRepository,
                            ActivityLogRepository activityLogRepository) {
        this.milestoneRepository = milestoneRepository;
        this.projectRepository = projectRepository;
        this.activityLogRepository = activityLogRepository;
    }

    @Transactional
    public Milestone createMilestone(Long projectId, String title, String description, LocalDate targetDeadline, double weightPercentage) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        Milestone milestone = new Milestone(project, title, description, targetDeadline, weightPercentage);
        Milestone saved = milestoneRepository.save(milestone);

        ActivityLog log = new ActivityLog(project, project.getOwner(), "MILESTONE_CREATED",
                "New milestone '" + title + "' (Target: " + targetDeadline + ") created.");
        activityLogRepository.save(log);

        return saved;
    }

    @Transactional
    public Milestone toggleMilestone(Long milestoneId) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new IllegalArgumentException("Milestone with ID " + milestoneId + " not found."));

        if (milestone.isCompleted()) {
            milestone.markIncomplete();
        } else {
            milestone.markComplete();
        }

        Milestone saved = milestoneRepository.save(milestone);

        ActivityLog log = new ActivityLog(milestone.getProject(), milestone.getProject().getOwner(),
                milestone.isCompleted() ? "MILESTONE_COMPLETED" : "MILESTONE_REOPENED",
                "Milestone '" + milestone.getTitle() + "' was marked " + (milestone.isCompleted() ? "COMPLETED" : "INCOMPLETE") + ".");
        activityLogRepository.save(log);

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Milestone> getMilestonesByProject(Long projectId) {
        return milestoneRepository.findByProjectIdOrderByTargetDeadlineAsc(projectId);
    }
}
