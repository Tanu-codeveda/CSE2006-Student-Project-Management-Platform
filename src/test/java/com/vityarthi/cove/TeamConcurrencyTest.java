package com.vityarthi.cove;

import com.vityarthi.cove.exception.TeamFullException;
import com.vityarthi.cove.model.Project;
import com.vityarthi.cove.model.Student;
import com.vityarthi.cove.model.Team;
import com.vityarthi.cove.repository.ActivityLogRepository;
import com.vityarthi.cove.repository.ProjectRepository;
import com.vityarthi.cove.repository.StudentRepository;
import com.vityarthi.cove.repository.TeamRepository;
import com.vityarthi.cove.service.NotificationService;
import com.vityarthi.cove.service.TeamManagementService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Concurrency Test Case demonstrating Thread Safety and Race Condition Prevention.
 * Demonstrates:
 * - CSE2006 Unit 3: Multithreading, CountDownLatch, ExecutorService, Synchronization.
 * - Simulates simultaneous slot claim by 10 threads when only 1 slot is available.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TeamConcurrencyTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ActivityLogRepository activityLogRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TeamManagementService teamManagementService;

    @Test
    @DisplayName("Should prevent race conditions: Exactly 1 of 10 concurrent threads successfully claims the final slot")
    void testConcurrentTeamSlotClaim() throws InterruptedException {
        int maxCapacity = 2; // Owner takes 1, exactly 1 slot left
        Student owner = new Student("Project Lead", "lead@vitbhopal.ac.in", "pass", "21BCE0001", "CSE", 3);
        owner.setId(1L);

        Project project = new Project("High Concurrency Engine", "Test concurrency engine", owner, maxCapacity, LocalDate.now().plusMonths(1));
        project.setId(50L);

        Team team = project.getTeam(); // Contains owner, current size = 1

        when(projectRepository.findById(50L)).thenReturn(Optional.of(project));
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch finishSignal = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger teamFullCount = new AtomicInteger(0);
        List<Throwable> unexpectedErrors = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            final long studentId = 100L + i;
            Student candidate = new Student("Candidate " + i, "cand" + i + "@vit.ac.in", "pass", "21BCE" + (2000 + i), "CSE", 2);
            candidate.setId(studentId);

            when(studentRepository.findById(studentId)).thenReturn(Optional.of(candidate));
            when(projectRepository.findProjectsByStudentMembership(studentId)).thenReturn(Collections.emptyList());

            executor.submit(() -> {
                try {
                    startSignal.await(); // Wait for simultaneous start
                    teamManagementService.addMemberToTeam(50L, studentId, "Developer");
                    successCount.incrementAndGet();
                } catch (TeamFullException e) {
                    teamFullCount.incrementAndGet();
                } catch (Throwable t) {
                    unexpectedErrors.add(t);
                } finally {
                    finishSignal.countDown();
                }
            });
        }

        // Fire all threads simultaneously
        startSignal.countDown();
        boolean completed = finishSignal.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(completed, "All concurrent tasks should complete within timeout");
        assertTrue(unexpectedErrors.isEmpty(), "No unexpected errors should occur: " + unexpectedErrors);

        // Verification: Exactly 1 thread got the slot, exactly 9 threads were rejected with TeamFullException
        assertEquals(1, successCount.get(), "Exactly one concurrent thread must succeed in taking the single available slot.");
        assertEquals(threadCount - 1, teamFullCount.get(), "Remaining 9 concurrent threads must receive TeamFullException.");
        assertEquals(maxCapacity, team.getMembers().size(), "Team size must strictly equal maximum capacity without over-subscription.");
    }
}
