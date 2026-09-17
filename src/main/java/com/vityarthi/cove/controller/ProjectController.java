package com.vityarthi.cove.controller;

import com.vityarthi.cove.dto.ProjectRequest;
import com.vityarthi.cove.model.Project;
import com.vityarthi.cove.model.ProjectStatus;
import com.vityarthi.cove.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<Project>> getProjects(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(projectService.searchProjects(search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProjectDetails(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        Map<String, Object> details = new HashMap<>();
        details.put("id", project.getId());
        details.put("title", project.getTitle());
        details.put("description", project.getDescription());
        details.put("status", project.getStatus());
        details.put("statusLabel", project.getStatus().getDisplayLabel());
        details.put("owner", project.getOwner());
        details.put("mentor", project.getMentor());
        details.put("deadline", project.getDeadline());
        details.put("techStack", project.getTechStack());
        details.put("teamSizeLimit", project.getTeamSizeLimit());
        details.put("team", project.getTeam());
        details.put("requiredSkills", project.getRequiredSkills());
        details.put("tasks", project.getTasks());
        details.put("milestones", project.getMilestones());
        details.put("progressPercentage", project.calculateProgressPercentage());
        details.put("isOverdue", project.isOverdue());
        return ResponseEntity.ok(details);
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestParam Long ownerId, @Valid @RequestBody ProjectRequest request) {
        Project created = projectService.createProject(ownerId, request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Project> updateStatus(@PathVariable Long id,
                                                @RequestParam Long userId,
                                                @RequestParam ProjectStatus status) {
        Project updated = projectService.updateStatus(id, userId, status);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/mentor")
    public ResponseEntity<Project> assignMentor(@PathVariable Long id, @RequestParam Long facultyId) {
        Project updated = projectService.assignMentor(id, facultyId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/by-student/{studentId}")
    public ResponseEntity<List<Project>> getStudentProjects(@PathVariable Long studentId) {
        return ResponseEntity.ok(projectService.getProjectsByStudentMembership(studentId));
    }
}
