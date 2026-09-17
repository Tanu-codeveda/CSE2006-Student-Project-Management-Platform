package com.vityarthi.cove.controller;

import com.vityarthi.cove.model.TeamMember;
import com.vityarthi.cove.service.TeamManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/team")
public class TeamController {

    private final TeamManagementService teamManagementService;

    @Autowired
    public TeamController(TeamManagementService teamManagementService) {
        this.teamManagementService = teamManagementService;
    }

    /**
     * Endpoint to join a project team (demonstrates synchronized slot allocation).
     */
    @PostMapping("/join")
    public ResponseEntity<TeamMember> joinTeam(@PathVariable Long projectId,
                                               @RequestParam Long studentId,
                                               @RequestParam(defaultValue = "Developer") String roleInTeam) {
        TeamMember member = teamManagementService.addMemberToTeam(projectId, studentId, roleInTeam);
        return ResponseEntity.ok(member);
    }

    @DeleteMapping("/members/{studentId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long projectId,
                                            @PathVariable Long studentId) {
        teamManagementService.removeMemberFromTeam(projectId, studentId);
        return ResponseEntity.noContent().build();
    }
}
