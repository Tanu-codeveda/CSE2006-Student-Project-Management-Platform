# Verification & Testing Strategy

## 1. Test Architecture & Methodology

Testing for **CoVe** was engineered to systematically validate functional correctness, exception hierarchies, multithreaded synchronization, and persistence layers.

Tests are located in `src/test/java/com/vityarthi/cove/` using **JUnit 5 (Jupiter)** and **Mockito**.

---

## 2. Test Suite Summary Matrix

| Test Class | Focus Area | Concepts Tested | Status |
| :--- | :--- | :--- | :--- |
| **`SkillMatchingServiceTest`** | Rule-Based Skill Compatibility | Method Overloading, Collections (`Set`, `Map`), Overlap Scoring, Explainability badges. | **PASSED** (3/3) |
| **`TeamConcurrencyTest`** | Race Condition Prevention | `ExecutorService`, `CountDownLatch`, `synchronized` blocks, `TeamFullException`. | **PASSED** (1/1) |
| **`ProjectLifecycleTest`** | State Machine Validation | Enums, state transitions, `InvalidProjectStateException`, terminal state protection. | **PASSED** (3/3) |
| **`TaskAndMilestoneProgressTest`** | Tasks & Progress Math | Task state guards, `PriorityQueue` scheduling by severity, weighted mathematical progress. | **PASSED** (4/4) |
| **`ReportExportIOTest`** | Java I/O Streams | Character streams (`PrintWriter`, `BufferedWriter`), Byte streams (`ByteArrayOutputStream`), CSV Importer (`BufferedReader`). | **PASSED** (3/3) |
| **`JdbcAnalyticsDaoTest`** | Direct JDBC Persistence | Raw SQL queries, `Connection`, `PreparedStatement`, `ResultSetMetaData` column normalization. | **PASSED** (2/2) |

**Total Automated Tests:** 16  
**Failures / Errors:** 0  
**Execution Time:** ~5 seconds

---

## 3. Deep Dive: Multithreaded Concurrency Test (`TeamConcurrencyTest`)

### Concurrency Scenario:
A project has a maximum team capacity of 2 members. The project lead occupies 1 slot, leaving **exactly 1 remaining slot**.

### Simulation:
1. Spawns 10 worker threads via `Executors.newFixedThreadPool(10)`.
2. All 10 threads block on a single `CountDownLatch startSignal = new CountDownLatch(1);`.
3. The main test thread calls `startSignal.countDown()`, releasing all 10 threads concurrently to invoke `teamManagementService.addMemberToTeam(...)` simultaneously.
4. Each thread records its outcome using thread-safe `AtomicInteger` counters (`successCount`, `teamFullCount`).

### Assertions:
```java
// Exactly 1 thread must succeed in securing the single remaining slot
assertEquals(1, successCount.get());

// The remaining 9 threads must be rejected with TeamFullException
assertEquals(9, teamFullCount.get());

// The team size must strictly equal maximum capacity (2) without over-subscription
assertEquals(2, team.getMembers().size());
```

---

## 4. Test Execution Instructions

To execute the automated test suite on Windows or Linux:

```powershell
# Using the bundled Maven wrapper
.\mvnw.cmd test

# Using standard Maven (if in PATH)
mvn test
```
