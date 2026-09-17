package com.vityarthi.cove.controller;

import com.vityarthi.cove.model.Skill;
import com.vityarthi.cove.model.SkillCategory;
import com.vityarthi.cove.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillRepository skillRepository;

    @Autowired
    public SkillController(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @GetMapping
    public ResponseEntity<List<Skill>> getAllSkills(@RequestParam(required = false) SkillCategory category) {
        if (category != null) {
            return ResponseEntity.ok(skillRepository.findByCategory(category));
        }
        return ResponseEntity.ok(skillRepository.findAllByOrderByNameAsc());
    }

    @PostMapping
    public ResponseEntity<Skill> createSkill(@RequestBody Skill skill) {
        if (skillRepository.existsByNameIgnoreCase(skill.getName())) {
            return ResponseEntity.badRequest().build();
        }
        Skill saved = skillRepository.save(skill);
        return ResponseEntity.ok(saved);
    }
}
