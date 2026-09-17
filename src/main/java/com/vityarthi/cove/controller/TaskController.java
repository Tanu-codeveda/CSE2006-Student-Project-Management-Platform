package com.vityarthi.cove.controller;

import com.vityarthi.cove.dto.TaskRequest;
import com.vityarthi.cove.model.Task;
import com.vityarthi.cove.model.TaskStatus;
import com.vityarthi.cove.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/api/projects/{projectId}/tasks")
    public ResponseEntity<List<Task>> getProjectTasks(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId));
    }

    @PostMapping("/api/projects/{projectId}/tasks")
    public ResponseEntity<Task> createTask(@PathVariable Long projectId,
                                           @RequestParam Long authorId,
                                           @Valid @RequestBody TaskRequest request) {
        Task created = taskService.createTask(projectId, authorId, request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/api/tasks/{taskId}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long taskId,
                                                 @RequestParam Long userId,
                                                 @RequestParam TaskStatus status) {
        Task updated = taskService.updateTaskStatus(taskId, userId, status);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/api/tasks/{taskId}/assign")
    public ResponseEntity<Task> assignTask(@PathVariable Long taskId, @RequestParam Long studentId) {
        Task updated = taskService.assignTask(taskId, studentId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/api/tasks/prioritized/{studentId}")
    public ResponseEntity<List<Task>> getPrioritizedTasks(@PathVariable Long studentId) {
        return ResponseEntity.ok(taskService.getPrioritizedTasksForAssignee(studentId));
    }

    @DeleteMapping("/api/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
