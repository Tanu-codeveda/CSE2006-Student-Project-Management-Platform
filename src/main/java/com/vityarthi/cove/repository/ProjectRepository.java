package com.vityarthi.cove.repository;

import com.vityarthi.cove.model.Project;
import com.vityarthi.cove.model.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerId(Long ownerId);
    List<Project> findByMentorId(Long mentorId);
    List<Project> findByStatus(ProjectStatus status);

    @Query("SELECT p FROM Project p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.techStack) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Project> searchProjects(@Param("keyword") String keyword);

    @Query("SELECT p FROM Project p JOIN p.team t JOIN t.members m WHERE m.student.id = :studentId")
    List<Project> findProjectsByStudentMembership(@Param("studentId") Long studentId);

    List<Project> findByDeadlineBeforeAndStatusNot(LocalDate date, ProjectStatus status);
}
