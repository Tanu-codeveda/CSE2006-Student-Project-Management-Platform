package com.vityarthi.cove.service;

import com.vityarthi.cove.dto.TaskRequest;
import com.vityarthi.cove.exception.InvalidTaskStateException;
import com.vityarthi.cove.exception.ProjectNotFoundException;
import com.vityarthi.cove.exception.UnauthorizedActionException;
import com.vityarthi.cove.exception.UserNotFoundException;
import com.vityarthi.cove.model.*;
import com.vityarthi.cove.repository.ActivityLogRepository;
import com.vityarthi.cove.repository.ProjectRepository;
import com.vityarthi.cove.repository.StudentRepository;
import com.vityarthi.cove.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service managing task lifecycles, priority scheduling, and assignments.
 * Demonstrates:
 * - CSE2006 Unit 2: Encapsulation.
 * - CSE2006 Unit 3: Exception handling and validation.
 * - CSE2006 Unit 4: PriorityQueue collection for priority-based task scheduling.
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final StudentRepository studentRepository;
    private final ActivityLogRepository activityLogRepository;
    private final NotificationService notificationService;

    @Autowired
    public TaskService(TaskRepository taskRepository,
                       ProjectRepository projectRepository,
                       StudentRepository studentRepository,
                       ActivityLogRepository activityLogRepository,
                       NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.studentRepository = studentRepository;
        this.activityLogRepository = activityLogRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Task createTask(Long projectId, Long authorId, TaskRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        Student assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = studentRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new UserNotFoundException(request.getAssigneeId()));
            
            // Verify assignee is in team
            if (project.getTeam() != null && !project.getTeam().hasStudent(assignee)) {
                throw new UnauthorizedActionException("Assignee must be a verified member of the project team.");
            }
        }

        Task task = new Task(
                project,
                request.getTitle(),
                request.getDescription(),
                assignee,
                request.getPriority(),
                request.getDeadline()
        );

        Task saved = taskRepository.save(task);

        ActivityLog log = new ActivityLog(project, assignee, "TASK_CREATED",
                "Task '" + task.getTitle() + "' created with priority " + task.getPriority() + ".");
        activityLogRepository.save(log);

        if (assignee != null) {
            notificationService.sendNotification(
                    assignee,
                    "New Task Assigned",
                    "You have been assigned to task '" + task.getTitle() + "' in project '" + project.getTitle() + "'.",
                    "TASK_ASSIGNED"
            );
        }

        return saved;
    }

    @Transactional
    public Task updateTaskStatus(Long taskId, Long userId, TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task with ID " + taskId + " not found."));

        // Transition with strict state validation (throws InvalidTaskStateException on failure)
        task.transitionTo(newStatus);
        Task saved = taskRepository.save(task);

        ActivityLog log = new ActivityLog(task.getProject(), task.getAssignee(), "TASK_STATUS_CHANGED",
                "Task '" + task.getTitle() + "' moved to " + newStatus.name() + ".");
        activityLogRepository.save(log);

        if (newStatus == TaskStatus.COMPLETED) {
            notificationService.sendNotification(
                    task.getProject().getOwner(),
                    "Task Completed",
                    "Task '" + task.getTitle() + "' was marked COMPLETED by " +
                            (task.getAssignee() != null ? task.getAssignee().getName() : "team member") + ".",
                    "TASK_COMPLETED"
            );
        }

        return saved;
    }

    @Transactional
    public Task assignTask(Long taskId, Long studentId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task with ID " + taskId + " not found."));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new UserNotFoundException(studentId));

        if (!task.getProject().getTeam().hasStudent(student)) {
            throw new UnauthorizedActionException("Assigned user must be an active member of this project team.");
        }

        task.setAssignee(student);
        Task saved = taskRepository.save(task);

        notificationService.sendNotification(
                student,
                "Task Assigned",
                "You are now assigned to task '" + task.getTitle() + "'.",
                "TASK_ASSIGNED"
        );

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    /**
     * Demonstrates PriorityQueue from Collections Framework (CSE2006 Unit 4).
     * Tasks are sorted by priority severity (CRITICAL > HIGH > MEDIUM > LOW) and deadline.
     */
    @Transactional(readOnly = true)
    public List<Task> getPrioritizedTasksForAssignee(Long studentId) {
        List<Task> tasks = taskRepository.findByAssigneeId(studentId);
        PriorityQueue<Task> priorityQueue = new PriorityQueue<>(tasks);
        List<Task> prioritized = new ArrayList<>();
        while (!priorityQueue.isEmpty()) {
            prioritized.add(priorityQueue.poll());
        }
        return prioritized;
    }

    @Transactional
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }
}
