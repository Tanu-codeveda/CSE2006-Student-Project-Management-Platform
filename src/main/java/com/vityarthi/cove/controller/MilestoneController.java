package com.vityarthi.cove.controller;

import com.vityarthi.cove.model.Milestone;
import com.vityarthi.cove.service.MilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MilestoneController {

    private final MilestoneService milestoneService;

    @Autowired
    public MilestoneController(MilestoneService milestoneService) {
        this.milestoneService = milestoneService;
    }

    @GetMapping("/projects/{projectId}/milestones")
    public ResponseEntity<List<Milestone>> getMilestones(@PathVariable Long projectId) {
        return ResponseEntity.ok(milestoneService.getMilestonesByProject(projectId));
    }

    @PostMapping("/projects/{projectId}/milestones")
    public ResponseEntity<Milestone> createMilestone(@PathVariable Long projectId,
                                                     @RequestBody Map<String, Object> body) {
        String title = (String) body.get("title");
        String description = (String) body.get("description");
        LocalDate deadline = LocalDate.parse((String) body.get("targetDeadline"));
        double weight = Double.parseDouble(body.getOrDefault("weightPercentage", 20.0).toString());

        Milestone created = milestoneService.createMilestone(projectId, title, description, deadline, weight);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/milestones/{milestoneId}/toggle")
    public ResponseEntity<Milestone> toggleMilestone(@PathVariable Long milestoneId) {
        Milestone updated = milestoneService.toggleMilestone(milestoneId);
        return ResponseEntity.ok(updated);
    }
}
