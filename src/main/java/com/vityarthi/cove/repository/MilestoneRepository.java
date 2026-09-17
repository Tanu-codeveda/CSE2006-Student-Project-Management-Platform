package com.vityarthi.cove.repository;

import com.vityarthi.cove.model.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
    List<Milestone> findByProjectId(Long projectId);
    List<Milestone> findByProjectIdOrderByTargetDeadlineAsc(Long projectId);
    List<Milestone> findByCompletedFalseAndTargetDeadlineBefore(LocalDate date);
}
