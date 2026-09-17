package com.vityarthi.cove;

import com.vityarthi.cove.exception.InvalidTaskStateException;
import com.vityarthi.cove.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests verifying task state transitions, priority scheduling, and milestone mathematical progress.
 * Demonstrates:
 * - CSE2006 Unit 1 & 4: Floating point math, PriorityQueue collection.
 */
public class TaskAndMilestoneProgressTest {

    private Project project;
    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student("Eve", "eve@vit.ac.in", "pass", "21BCE1300", "CSE", 3);
        project = new Project("Crypto Ledger", "Distributed ledger system", student, 4, LocalDate.now().plusMonths(2));
    }

    @Test
    @DisplayName("Should validate valid task progression: TODO -> IN_PROGRESS -> COMPLETED")
    void testValidTaskTransition() {
        Task task = new Task(project, "Build Genesis Block", "Hash initialization", student, TaskPriority.HIGH, LocalDate.now().plusDays(3));
        assertEquals(TaskStatus.TODO, task.getStatus());

        task.transitionTo(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());

        task.transitionTo(TaskStatus.COMPLETED);
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertNotNull(task.getCompletedAt());
    }

    @Test
    @DisplayName("Should reject illegal task jump directly from TODO to COMPLETED")
    void testInvalidTaskTransition() {
        Task task = new Task(project, "Write Consensus Algorithm", "Raft protocol", student, TaskPriority.CRITICAL, LocalDate.now().plusDays(5));

        assertThrows(InvalidTaskStateException.class, () -> {
            task.transitionTo(TaskStatus.COMPLETED);
        });
    }

    @Test
    @DisplayName("Should correctly prioritize tasks in PriorityQueue by severity and deadline")
    void testPriorityQueueOrdering() {
        Task lowTask = new Task(project, "Update Docs", "Doc updates", student, TaskPriority.LOW, LocalDate.now().plusDays(1));
        Task criticalTask = new Task(project, "Patch Exploit", "Security patch", student, TaskPriority.CRITICAL, LocalDate.now().plusDays(2));
        Task highTask = new Task(project, "Database Indexing", "Index query optimization", student, TaskPriority.HIGH, LocalDate.now().plusDays(1));

        PriorityQueue<Task> queue = new PriorityQueue<>();
        queue.add(lowTask);
        queue.add(criticalTask);
        queue.add(highTask);

        // Polling from priority queue must return CRITICAL first, then HIGH, then LOW
        assertEquals(TaskPriority.CRITICAL, queue.poll().getPriority());
        assertEquals(TaskPriority.HIGH, queue.poll().getPriority());
        assertEquals(TaskPriority.LOW, queue.poll().getPriority());
    }

    @Test
    @DisplayName("Should compute combined milestone and task mathematical progress accurately")
    void testProjectProgressPercentageCalculation() {
        Milestone m1 = new Milestone(project, "Phase 1: Architecture", "Spec design", LocalDate.now(), 50.0);
        Milestone m2 = new Milestone(project, "Phase 2: Code Delivery", "Delivery", LocalDate.now().plusMonths(1), 50.0);
        m1.markComplete(); // 50% milestone complete

        project.getMilestones().add(m1);
        project.getMilestones().add(m2);

        Task t1 = new Task(project, "Task 1", "Task 1", student, TaskPriority.MEDIUM, LocalDate.now().plusDays(5));
        Task t2 = new Task(project, "Task 2", "Task 2", student, TaskPriority.MEDIUM, LocalDate.now().plusDays(5));
        t1.transitionTo(TaskStatus.IN_PROGRESS);
        t1.transitionTo(TaskStatus.COMPLETED); // 50% tasks complete

        project.getTasks().add(t1);
        project.getTasks().add(t2);

        // Expected: 0.6 * (50%) + 0.4 * (50%) = 30% + 20% = 50.0%
        double progress = project.calculateProgressPercentage();
        assertEquals(50.0, progress, 0.1);
    }
}
