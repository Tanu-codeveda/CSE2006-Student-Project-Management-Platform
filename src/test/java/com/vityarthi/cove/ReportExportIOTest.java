package com.vityarthi.cove;

import com.vityarthi.cove.io.ByteStreamDataExporter;
import com.vityarthi.cove.io.CharacterStreamReportExporter;
import com.vityarthi.cove.io.SkillCatalogCsvImporter;
import com.vityarthi.cove.model.*;
import com.vityarthi.cove.repository.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests verifying Java I/O Streams functionality (Character Streams and Byte Streams).
 * Demonstrates:
 * - CSE2006 Unit 4 & Indicative Experiment 16: Java I/O Streams.
 */
@ExtendWith(MockitoExtension.class)
public class ReportExportIOTest {

    @Mock
    private SkillRepository skillRepository;

    private CharacterStreamReportExporter characterExporter;
    private ByteStreamDataExporter byteExporter;
    private SkillCatalogCsvImporter csvImporter;

    private Project project;
    private Student lead;

    @BeforeEach
    void setUp() {
        characterExporter = new CharacterStreamReportExporter();
        byteExporter = new ByteStreamDataExporter();
        csvImporter = new SkillCatalogCsvImporter(skillRepository);

        lead = new Student("Lead Dev", "lead@vit.ac.in", "pass", "21BCE1400", "CSE", 3);
        project = new Project("Autonomous Drone Swarm", "Autonomous multi-drone mesh coordination", lead, 4, LocalDate.now().plusMonths(3));
        project.setId(10L);
    }

    @Test
    @DisplayName("Should generate formatted project dossier via Character Streams (PrintWriter/BufferedWriter)")
    void testCharacterStreamExport() throws IOException {
        String dossier = characterExporter.exportProjectDossier(project);

        assertNotNull(dossier);
        assertTrue(dossier.contains("ACADEMIC PROJECT DOSSIER & EVALUATION REPORT"));
        assertTrue(dossier.contains("Autonomous Drone Swarm"));
        assertTrue(dossier.contains("Lead Dev"));
        assertTrue(dossier.contains("21BCE1400"));
        assertTrue(dossier.contains("END OF DOSSIER"));
    }

    @Test
    @DisplayName("Should export tasks to CSV byte array via Byte Streams (ByteArrayOutputStream/BufferedOutputStream)")
    void testByteStreamCsvExport() throws IOException {
        Task t = new Task(project, "Kalman Filter Implementation", "State estimation", lead, TaskPriority.CRITICAL, LocalDate.now().plusWeeks(1));
        t.setId(101L);

        byte[] csvBytes = byteExporter.exportTasksToCsv(List.of(t));

        assertNotNull(csvBytes);
        assertTrue(csvBytes.length > 0);

        String csvString = new String(csvBytes, StandardCharsets.UTF_8);
        assertTrue(csvString.contains("Task ID,Title,Assignee Name"));
        assertTrue(csvString.contains("101,Kalman Filter Implementation,Lead Dev"));
    }

    @Test
    @DisplayName("Should parse and ingest CSV taxonomy via Character Streams (BufferedReader/InputStreamReader)")
    void testCharacterStreamCsvImporter() throws IOException {
        String csvData = "Name,Category,Description\n" +
                "Rust,PROGRAMMING,Systems language with memory safety guarantees\n" +
                "GraphQL,BACKEND,Query language for APIs\n";

        when(skillRepository.existsByNameIgnoreCase("Rust")).thenReturn(false);
        when(skillRepository.existsByNameIgnoreCase("GraphQL")).thenReturn(false);

        ByteArrayInputStream is = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
        int imported = csvImporter.importFromInputStream(is);

        assertEquals(2, imported);
        verify(skillRepository, org.mockito.Mockito.times(2)).save(any(Skill.class));
    }
}
