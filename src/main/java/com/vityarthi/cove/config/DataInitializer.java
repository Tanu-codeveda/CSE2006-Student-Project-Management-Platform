package com.vityarthi.cove.config;

import com.vityarthi.cove.dto.ProjectRequest;
import com.vityarthi.cove.io.SkillCatalogCsvImporter;
import com.vityarthi.cove.model.*;
import com.vityarthi.cove.repository.*;
import com.vityarthi.cove.service.ProjectService;
import com.vityarthi.cove.service.TeamManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Initializes catalog taxonomy from CSV using I/O streams and seeds realistic academic data.
 * Demonstrates:
 * - CSE2006 Unit 4: I/O Streams CSV ingestion.
 * - CSE2006 Unit 5: JPA Data population.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final SkillCatalogCsvImporter csvImporter;
    private final SkillRepository skillRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AdministratorRepository administratorRepository;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final TeamManagementService teamManagementService;
    private final TaskRepository taskRepository;
    private final MilestoneRepository milestoneRepository;

    @Autowired
    public DataInitializer(SkillCatalogCsvImporter csvImporter,
                           SkillRepository skillRepository,
                           StudentRepository studentRepository,
                           FacultyRepository facultyRepository,
                           AdministratorRepository administratorRepository,
                           ProjectRepository projectRepository,
                           ProjectService projectService,
                           TeamManagementService teamManagementService,
                           TaskRepository taskRepository,
                           MilestoneRepository milestoneRepository) {
        this.csvImporter = csvImporter;
        this.skillRepository = skillRepository;
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.administratorRepository = administratorRepository;
        this.projectRepository = projectRepository;
        this.projectService = projectService;
        this.teamManagementService = teamManagementService;
        this.taskRepository = taskRepository;
        this.milestoneRepository = milestoneRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("[Initialization] Starting automated system seeding...");

        // 1. Ingest Skills Catalog from CSV via Character Streams
        if (skillRepository.count() == 0) {
            csvImporter.importFromResource(new ClassPathResource("data/skills-catalog.csv"));
        }

        // 2. Seed Administrator
        if (administratorRepository.count() == 0) {
            Administrator admin = new Administrator("System Admin", "admin@vitbhopal.ac.in", "admin123", "ADM-001", true);
            administratorRepository.save(admin);
        }

        // 3. Seed Faculty Mentors
        Faculty mentorAshwin = null;
        Faculty mentorLakshmi = null;
        if (facultyRepository.count() == 0) {
            mentorAshwin = new Faculty("Dr. M. Ashwin", "ashwin.m@vitbhopal.ac.in", "faculty123", "FAC-1001", "Computer Science & Engineering", "Associate Professor");
            mentorLakshmi = new Faculty("Dr. Lakshmi D", "lakshmi.d@vitbhopal.ac.in", "faculty123", "FAC-1002", "Software Systems & Computing", "Professor & HoD");
            mentorAshwin = facultyRepository.save(mentorAshwin);
            mentorLakshmi = facultyRepository.save(mentorLakshmi);
        } else {
            mentorAshwin = facultyRepository.findAll().get(0);
        }

        // 4. Seed Students with realistic skills
        if (studentRepository.count() == 0) {
            Student s1 = new Student("Tanu Gowda", "tanu.gowda@vitbhopal.ac.in", "student123", "21BCE1001", "Computer Science & Engineering", 3, "Passionate full-stack developer with interest in distributed systems and cloud.");
            Student s2 = new Student("Rahul Sharma", "rahul.sharma@vitbhopal.ac.in", "student123", "21BCE1045", "Computer Science & Engineering", 3, "Backend enthusiast focusing on high-concurrency Java and microservices.");
            Student s3 = new Student("Priya Patel", "priya.patel@vitbhopal.ac.in", "student123", "21BCE1092", "Information Technology", 3, "Frontend engineer specializing in responsive interactive web interfaces.");
            Student s4 = new Student("Sneha Verma", "sneha.verma@vitbhopal.ac.in", "student123", "22BCE2011", "Computer Science & Engineering", 2, "Database architect with strong knowledge of SQL query optimization.");
            Student s5 = new Student("Amit Kumar", "amit.kumar@vitbhopal.ac.in", "student123", "22BCE2088", "Cybersecurity", 2, "Security researcher with interest in cryptography and secure coding.");

            s1 = studentRepository.save(s1);
            s2 = studentRepository.save(s2);
            s3 = studentRepository.save(s3);
            s4 = studentRepository.save(s4);
            s5 = studentRepository.save(s5);

            // Assign skills with varying proficiencies
            assignSkill(s1, "Java", ProficiencyLevel.ADVANCED, 2.5);
            assignSkill(s1, "SQL", ProficiencyLevel.INTERMEDIATE, 2.0);
            assignSkill(s1, "Spring Boot", ProficiencyLevel.INTERMEDIATE, 1.5);
            assignSkill(s1, "Docker", ProficiencyLevel.BEGINNER, 1.0);

            assignSkill(s2, "Java", ProficiencyLevel.EXPERT, 3.0);
            assignSkill(s2, "Spring Boot", ProficiencyLevel.ADVANCED, 2.5);
            assignSkill(s2, "SQL", ProficiencyLevel.ADVANCED, 2.0);
            assignSkill(s2, "Cloud Computing", ProficiencyLevel.INTERMEDIATE, 1.5);

            assignSkill(s3, "JavaScript", ProficiencyLevel.EXPERT, 3.0);
            assignSkill(s3, "React", ProficiencyLevel.ADVANCED, 2.0);
            assignSkill(s3, "UI/UX Design", ProficiencyLevel.ADVANCED, 2.0);
            assignSkill(s3, "TypeScript", ProficiencyLevel.INTERMEDIATE, 1.5);

            assignSkill(s4, "SQL", ProficiencyLevel.EXPERT, 3.0);
            assignSkill(s4, "PostgreSQL", ProficiencyLevel.ADVANCED, 2.0);
            assignSkill(s4, "Python", ProficiencyLevel.INTERMEDIATE, 1.5);
            assignSkill(s4, "Database", ProficiencyLevel.ADVANCED, 2.5);

            assignSkill(s5, "Cybersecurity", ProficiencyLevel.ADVANCED, 2.0);
            assignSkill(s5, "Cryptography", ProficiencyLevel.INTERMEDIATE, 1.5);
            assignSkill(s5, "Python", ProficiencyLevel.ADVANCED, 2.0);

            // 5. Create Sample Projects
            ProjectRequest p1Req = new ProjectRequest();
            p1Req.setTitle("Student Project & Team Management Platform");
            p1Req.setDescription("A comprehensive collaboration platform that allows university students to discover compatible teammates via explainable rule-based skill matching, manage project tasks, and track academic milestones.");
            p1Req.setTechStack("Java 17, Spring Boot, JPA/Hibernate, Direct JDBC, HTML5/CSS3");
            p1Req.setTeamSizeLimit(4);
            p1Req.setDeadline(LocalDate.now().plusMonths(2));
            p1Req.setRequiredSkills(List.of(
                    new ProjectRequest.SkillRequirement("Java", ProficiencyLevel.INTERMEDIATE, 1.5),
                    new ProjectRequest.SkillRequirement("SQL", ProficiencyLevel.INTERMEDIATE, 1.2),
                    new ProjectRequest.SkillRequirement("Spring Boot", ProficiencyLevel.BEGINNER, 1.0),
                    new ProjectRequest.SkillRequirement("UI/UX Design", ProficiencyLevel.BEGINNER, 0.8)
            ));
            Project proj1 = projectService.createProject(s1.getId(), p1Req);
            proj1.validateAndSetStatus(ProjectStatus.IN_PROGRESS);
            proj1.setMentor(mentorAshwin);
            proj1 = projectRepository.save(proj1);

            // Add Rahul and Priya to the team
            teamManagementService.addMemberToTeam(proj1.getId(), s2.getId(), "Backend Architect");
            teamManagementService.addMemberToTeam(proj1.getId(), s3.getId(), "Frontend Lead");

            // Add Milestones to Project 1
            Milestone m1 = new Milestone(proj1, "Requirement Analysis & Scope Specification", "Detailed SRS and syllabus mapping", LocalDate.now().minusWeeks(2), 20.0);
            m1.markComplete();
            milestoneRepository.save(m1);

            Milestone m2 = new Milestone(proj1, "System Architecture & Database Design", "ER diagrams, schema definition, and UML models", LocalDate.now().minusWeeks(1), 25.0);
            m2.markComplete();
            milestoneRepository.save(m2);

            Milestone m3 = new Milestone(proj1, "Core Implementation & Rule-Based Matching", "Develop domain model, matching algorithm, and multithreaded daemon", LocalDate.now().plusWeeks(2), 30.0);
            milestoneRepository.save(m3);

            Milestone m4 = new Milestone(proj1, "Testing, Concurrency Auditing & Documentation", "JUnit test execution, I/O report exports, and final dossier", LocalDate.now().plusWeeks(4), 25.0);
            milestoneRepository.save(m4);

            // Add Tasks to Project 1
            Task t1 = new Task(proj1, "Design Domain Model and Inheritance Structure", "Create User, Student, Faculty, and Project entities with encapsulation", s1, TaskPriority.HIGH, LocalDate.now().minusDays(5));
            t1.transitionTo(TaskStatus.IN_PROGRESS);
            t1.transitionTo(TaskStatus.COMPLETED);
            taskRepository.save(t1);

            Task t2 = new Task(proj1, "Implement Rule-Based Skill Compatibility Matcher", "Develop explainable matching logic with transparent score breakdowns", s2, TaskPriority.CRITICAL, LocalDate.now().plusDays(5));
            t2.transitionTo(TaskStatus.IN_PROGRESS);
            taskRepository.save(t2);

            Task t3 = new Task(proj1, "Build Responsive Kanban Task Board UI", "Create drag-and-click task state transition cards", s3, TaskPriority.MEDIUM, LocalDate.now().plusDays(10));
            taskRepository.save(t3);

            Task t4 = new Task(proj1, "Audit Concurrent Join Race Conditions", "Implement JUnit multithreaded race condition prevention tests", s1, TaskPriority.HIGH, LocalDate.now().plusDays(14));
            taskRepository.save(t4);

            // Seed Project 2: Open for recruitment
            ProjectRequest p2Req = new ProjectRequest();
            p2Req.setTitle("AI-Powered Smart Campus Navigation & Resource Locator");
            p2Req.setDescription("Campus indoor positioning and real-time lab equipment availability tracking for university departments.");
            p2Req.setTechStack("Python, React, PostgreSQL, Docker");
            p2Req.setTeamSizeLimit(3);
            p2Req.setDeadline(LocalDate.now().plusMonths(3));
            p2Req.setRequiredSkills(List.of(
                    new ProjectRequest.SkillRequirement("Python", ProficiencyLevel.ADVANCED, 1.5),
                    new ProjectRequest.SkillRequirement("React", ProficiencyLevel.INTERMEDIATE, 1.2),
                    new ProjectRequest.SkillRequirement("PostgreSQL", ProficiencyLevel.INTERMEDIATE, 1.0)
            ));
            Project proj2 = projectService.createProject(s4.getId(), p2Req);
            proj2.validateAndSetStatus(ProjectStatus.OPEN);
            projectRepository.save(proj2);

            log.info("[Initialization] Successfully seeded demo students, faculty, projects, milestones, and tasks.");
        }
    }

    private void assignSkill(Student student, String skillName, ProficiencyLevel level, double years) {
        skillRepository.findByNameIgnoreCase(skillName).ifPresent(skill -> {
            student.addSkill(skill, level, years);
            studentRepository.save(student);
        });
    }
}
