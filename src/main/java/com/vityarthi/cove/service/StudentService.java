package com.vityarthi.cove.service;

import com.vityarthi.cove.exception.SkillNotFoundException;
import com.vityarthi.cove.exception.UserNotFoundException;
import com.vityarthi.cove.model.ProficiencyLevel;
import com.vityarthi.cove.model.Skill;
import com.vityarthi.cove.model.Student;
import com.vityarthi.cove.repository.SkillRepository;
import com.vityarthi.cove.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final SkillRepository skillRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository, SkillRepository skillRepository) {
        this.studentRepository = studentRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional
    public Student addSkillToStudent(Long studentId, String skillName, ProficiencyLevel level, double yearsExperience) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new UserNotFoundException(studentId));

        Skill skill = skillRepository.findByNameIgnoreCase(skillName)
                .orElseThrow(() -> new SkillNotFoundException(skillName));

        student.addSkill(skill, level, yearsExperience);
        return studentRepository.save(student);
    }

    @Transactional
    public Student removeSkillFromStudent(Long studentId, String skillName) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new UserNotFoundException(studentId));

        Skill skill = skillRepository.findByNameIgnoreCase(skillName)
                .orElseThrow(() -> new SkillNotFoundException(skillName));

        student.removeSkill(skill);
        return studentRepository.save(student);
    }

    @Transactional(readOnly = true)
    public Student getStudentProfile(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new UserNotFoundException(studentId));
    }

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}
