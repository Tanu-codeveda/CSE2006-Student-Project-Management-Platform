package com.vityarthi.cove.service;

import com.vityarthi.cove.exception.ProjectNotFoundException;
import com.vityarthi.cove.exception.TeamFullException;
import com.vityarthi.cove.exception.UnauthorizedActionException;
import com.vityarthi.cove.exception.UserNotFoundException;
import com.vityarthi.cove.model.*;
import com.vityarthi.cove.repository.ActivityLogRepository;
import com.vityarthi.cove.repository.ProjectRepository;
import com.vityarthi.cove.repository.StudentRepository;
import com.vityarthi.cove.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service managing team operations with thread-safe slot allocation.
 * Demonstrates:
 * - CSE2006 Unit 3: Java Synchronization and race condition prevention.
 * - CSE2006 Unit 4: Collections Framework.
 * - CSE2006 Unit 5: JPA Transactions and database consistency.
 */
@Service
public class TeamManagementService {

    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final StudentRepository studentRepository;
    private final ActivityLogRepository activityLogRepository;
    private final NotificationService notificationService;

    // Mutex for in-memory synchronization guarantees across threads
    private final ReentrantLock teamLock = new ReentrantLock(true); // Fair lock

    @Autowired
    public TeamManagementService(TeamRepository teamRepository,
                                 ProjectRepository projectRepository,
                                 StudentRepository studentRepository,
                                 ActivityLogRepository activityLogRepository,
                                 NotificationService notificationService) {
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.studentRepository = studentRepository;
        this.activityLogRepository = activityLogRepository;
        this.notificationService = notificationService;
    }

    /**
     * Adds a student to a project's team with explicit thread-safe synchronization.
     * Prevents race condition when multiple concurrent users attempt to claim the last open slot.
     */
    @Transactional
    public TeamMember addMemberToTeam(Long projectId, Long studentId, String roleInTeam) {
        teamLock.lock();
        try {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ProjectNotFoundException(projectId));

            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new UserNotFoundException(studentId));

            Team team = project.getTeam();
            if (team == null) {
                team = new Team(project, project.getTeamSizeLimit());
                project.setTeam(team);
            }

            // Invariant 1: Check maximum team capacity (Race condition prevention)
            if (team.getMembers().size() >= team.getMaxMembers()) {
                throw new TeamFullException(team.getMembers().size(), team.getMaxMembers());
            }

            // Invariant 2: Check student already in team
            if (team.hasStudent(student)) {
                throw new IllegalArgumentException("Student " + student.getName() + " is already enrolled in this team.");
            }

            // Invariant 3: Check student's overall project concurrency limit
            List<Project> studentProjects = projectRepository.findProjectsByStudentMembership(studentId);
            if (studentProjects.size() >= student.getMaxActiveProjects()) {
                throw new UnauthorizedActionException("Student has reached their maximum allowed active projects limit (" + student.getMaxActiveProjects() + ").");
            }

            // Atomic membership addition via synchronized domain method
            team.addMemberSynchronized(student, roleInTeam);
            teamRepository.save(team);

            // Audit log
            ActivityLog log = new ActivityLog(project, student, "MEMBER_JOINED",
                    student.getName() + " (" + student.getRegistrationNumber() + ") joined the team as " + roleInTeam + ".");
            activityLogRepository.save(log);

            // Notify project owner
            notificationService.sendNotification(
                    project.getOwner(),
                    "New Team Member Joined",
                    student.getName() + " has joined " + project.getTitle() + " as " + roleInTeam + ".",
                    "TEAM_UPDATE"
            );

            return team.getMembers().stream()
                    .filter(m -> m.getStudent().equals(student))
                    .findFirst()
                    .orElseThrow();

        } finally {
            teamLock.unlock();
        }
    }

    /**
     * Removes a student member from a project team.
     */
    @Transactional
    public void removeMemberFromTeam(Long projectId, Long studentId) {
        teamLock.lock();
        try {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ProjectNotFoundException(projectId));

            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new UserNotFoundException(studentId));

            if (project.getOwner().equals(student)) {
                throw new UnauthorizedActionException("The project lead/owner cannot leave or be removed from the team.");
            }

            Team team = project.getTeam();
            if (team != null) {
                boolean removed = team.removeMemberSynchronized(student);
                if (removed) {
                    teamRepository.save(team);
                    ActivityLog log = new ActivityLog(project, student, "MEMBER_LEFT",
                            student.getName() + " left the team.");
                    activityLogRepository.save(log);
                }
            }
        } finally {
            teamLock.unlock();
        }
    }
}
