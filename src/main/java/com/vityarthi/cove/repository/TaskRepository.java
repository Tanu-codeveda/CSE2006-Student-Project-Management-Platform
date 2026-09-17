package com.vityarthi.cove.repository;

import com.vityarthi.cove.model.Task;
import com.vityarthi.cove.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);
    List<Task> findByAssigneeId(Long assigneeId);
    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.status <> 'COMPLETED' AND t.deadline < :currentDate")
    List<Task> findOverdueTasks(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT t FROM Task t WHERE t.status <> 'COMPLETED' AND t.deadline BETWEEN :currentDate AND :approachingDate")
    List<Task> findApproachingDeadlineTasks(@Param("currentDate") LocalDate currentDate, @Param("approachingDate") LocalDate approachingDate);
}
