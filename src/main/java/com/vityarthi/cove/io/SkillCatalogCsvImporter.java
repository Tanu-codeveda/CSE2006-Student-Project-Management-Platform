package com.vityarthi.cove.io;

import com.vityarthi.cove.model.Skill;
import com.vityarthi.cove.model.SkillCategory;
import com.vityarthi.cove.repository.SkillRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Character-Oriented Stream Importer for Skill Catalog Ingestion.
 * Demonstrates:
 * - CSE2006 Unit 4 & Indicative Experiment 16: Character Streams (BufferedReader, InputStreamReader).
 */
@Component
public class SkillCatalogCsvImporter {

    private static final Logger log = LoggerFactory.getLogger(SkillCatalogCsvImporter.class);

    private final SkillRepository skillRepository;

    @Autowired
    public SkillCatalogCsvImporter(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    /**
     * Reads a CSV input stream and seeds the skill taxonomy catalog into the database.
     */
    public int importFromInputStream(InputStream inputStream) throws IOException {
        int importedCount = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (isHeader) {
                    isHeader = false;
                    continue; // Skip header line
                }

                String[] tokens = line.split(",", 3);
                if (tokens.length >= 2) {
                    String name = tokens[0].trim();
                    String categoryStr = tokens[1].trim();
                    String description = tokens.length > 2 ? tokens[2].trim() : "";

                    SkillCategory category;
                    try {
                        category = SkillCategory.valueOf(categoryStr.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        category = SkillCategory.OTHER;
                    }

                    if (!skillRepository.existsByNameIgnoreCase(name)) {
                        Skill skill = new Skill(name, category, description);
                        skillRepository.save(skill);
                        importedCount++;
                    }
                }
            }
        }
        log.info("[I/O Streams] Successfully imported {} new skills via Character Stream reader.", importedCount);
        return importedCount;
    }

    public int importFromResource(Resource resource) throws IOException {
        if (!resource.exists()) {
            log.warn("[I/O Streams] Resource does not exist: {}", resource);
            return 0;
        }
        try (InputStream is = resource.getInputStream()) {
            return importFromInputStream(is);
        }
    }
}
