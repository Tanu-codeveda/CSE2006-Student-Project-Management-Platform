package com.vityarthi.cove.controller;

import com.vityarthi.cove.io.ByteStreamDataExporter;
import com.vityarthi.cove.io.CharacterStreamReportExporter;
import com.vityarthi.cove.model.Project;
import com.vityarthi.cove.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Controller managing file reporting exports via Java Character and Byte I/O Streams.
 * Demonstrates:
 * - CSE2006 Unit 4 & Indicative Experiment 16: Java I/O Streams for file downloads.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportExportController {

    private final ProjectService projectService;
    private final CharacterStreamReportExporter characterStreamExporter;
    private final ByteStreamDataExporter byteStreamExporter;

    @Autowired
    public ReportExportController(ProjectService projectService,
                                  CharacterStreamReportExporter characterStreamExporter,
                                  ByteStreamDataExporter byteStreamExporter) {
        this.projectService = projectService;
        this.characterStreamExporter = characterStreamExporter;
        this.byteStreamExporter = byteStreamExporter;
    }

    /**
     * Character-stream generated academic dossier (TXT/MD).
     */
    @GetMapping(value = "/projects/{projectId}/dossier", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> downloadProjectDossier(@PathVariable Long projectId) throws IOException {
        Project project = projectService.getProjectById(projectId);
        String dossier = characterStreamExporter.exportProjectDossier(project);

        String filename = "project-" + projectId + "-dossier.txt";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(dossier);
    }

    /**
     * Byte-stream generated tasks CSV export.
     */
    @GetMapping(value = "/projects/{projectId}/tasks-csv", produces = "text/csv")
    public ResponseEntity<byte[]> downloadTasksCsv(@PathVariable Long projectId) throws IOException {
        Project project = projectService.getProjectById(projectId);
        byte[] csvBytes = byteStreamExporter.exportTasksToCsv(project.getTasks());

        String filename = "project-" + projectId + "-tasks.csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(csvBytes);
    }

    /**
     * Byte-stream generated team roster CSV export.
     */
    @GetMapping(value = "/projects/{projectId}/team-csv", produces = "text/csv")
    public ResponseEntity<byte[]> downloadTeamCsv(@PathVariable Long projectId) throws IOException {
        Project project = projectService.getProjectById(projectId);
        byte[] csvBytes = byteStreamExporter.exportTeamRosterToCsv(project.getTeam().getMembers());

        String filename = "project-" + projectId + "-team.csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(csvBytes);
    }
}
