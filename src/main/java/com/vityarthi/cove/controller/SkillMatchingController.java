package com.vityarthi.cove.controller;

import com.vityarthi.cove.dto.SkillMatchResponse;
import com.vityarthi.cove.service.SkillMatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/matches")
public class SkillMatchingController {

    private final SkillMatchingService skillMatchingService;

    @Autowired
    public SkillMatchingController(SkillMatchingService skillMatchingService) {
        this.skillMatchingService = skillMatchingService;
    }

    /**
     * Executes the explainable rule-based matching engine for a project.
     */
    @GetMapping
    public ResponseEntity<List<SkillMatchResponse>> getMatchingCandidates(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0.0") double minThreshold) {
        List<SkillMatchResponse> matches = skillMatchingService.findMatchingCandidates(projectId, minThreshold);
        return ResponseEntity.ok(matches);
    }
}
