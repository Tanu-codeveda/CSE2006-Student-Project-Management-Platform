# Architectural & Design Decisions

This document details the engineering rationale and trade-offs made during the design of **CoVe (Student Project & Team Management Platform)**.

---

## 1. Rule-Based Matching vs. Machine Learning

### Decision:
We deliberately selected a **deterministic, rule-based skill compatibility matching algorithm** rather than a machine learning or statistical NLP model.

### Rationale:
1. **Explainability & Transparency**: In academic team formation, students and evaluators demand to know *why* a teammate was recommended. Our algorithm outputs an exact breakdown (`Java: MATCH ✓`, `SQL: PARTIAL ⚠`, `Docker: MISSING ✗`), making the score ($3/3$ or $66.7\%$) completely transparent.
2. **Cold-Start Resilience**: Machine learning models (e.g. collaborative filtering) fail when new students or projects are introduced without prior interaction data. Rule-based set overlap functions reliably on day one.
3. **CSE2006 Syllabus Relevance**: The rule-based implementation actively demonstrates:
   - Method Overloading (`evaluateStudent(student, project)`, `evaluateStudent(student, project, strict)`).
   - Java Collections Framework (`Set`, `Map`, `Collections.sort()`, `Comparable`).
   - Control Flow (`for-each`, conditional branch weighting).

---

## 2. Synchronization & Race Condition Prevention

### Concurrency Challenge:
Suppose a popular project has a team capacity limit of 4 members, and currently 3 members are enrolled (exactly 1 vacancy remaining). Two students click "Join Team" at the exact same millisecond across concurrent HTTP request threads.

### Unsynchronized Failure Mode:
Without synchronization:
1. Thread A checks `members.size() < 4` (evaluates to $3 < 4$ -> `true`).
2. Thread B checks `members.size() < 4` before Thread A has finished persisting (also evaluates to $3 < 4$ -> `true`).
3. Both threads insert their respective student records.
4. Total team size becomes 5, violating the hard capacity invariant!

### Implemented Solution:
We implemented **dual-layer concurrency protection**:
1. **In-Memory Java Synchronization**:
   - `TeamManagementService` utilizes a fair `ReentrantLock` (`teamLock = new ReentrantLock(true)`).
   - In addition, the domain method `Team.addMemberSynchronized(...)` is marked `synchronized`, protecting the collection's atomic invariant.
   - The first thread to acquire the lock claims the final slot; the subsequent thread encounters `size == maxCapacity` and receives an immediate `TeamFullException(size, max)`.
2. **Database Optimistic Locking**:
   - The `Team` entity includes `@Version private Long version;`. If two database transactions race at the DB tier, Hibernate issues an `OptimisticLockException`.
3. **Rigorous Verification**:
   - Verified via JUnit test `TeamConcurrencyTest.java` using `ExecutorService` and `CountDownLatch` with 10 concurrent threads racing for 1 slot. Exactly 1 succeeds, and 9 receive `TeamFullException`.

---

## 3. Dual Persistence Architecture (JPA + Direct JDBC)

### Decision:
We integrated both **Spring Data JPA (Hibernate)** and **Direct JDBC (`java.sql.*`)** in the same platform.

### Rationale:
1. **JPA for Relational Entity Graphs**:
   - Simplifies domain modeling for complex multi-entity hierarchies: `User` -> `Student` / `Faculty`, cascades on `Team` -> `TeamMember`, and lifecycle audits.
2. **Direct JDBC for Analytical Reporting (CSE2006 Syllabus Exp 14)**:
   - High-performance aggregate queries (e.g., cross-project skill market distribution and student workload analytics) are executed directly using `Connection`, `PreparedStatement`, `ResultSet`, and `ResultSetMetaData`.
   - This directly fulfills Syllabus Unit 5: *“Specifying JDBC driver information externally, submitting queries and getting results from the database using ResultSetMetaData”*.

---

## 4. Multithreaded Background Daemon

### Decision:
A dedicated background worker thread (`DeadlineMonitoringDaemon`) extends `Thread` and runs continuously throughout the application lifecycle.

### Rationale:
1. **Decoupled User Experience**: Auditing hundreds of tasks for upcoming or overdue deadlines should never add latency to student web requests.
2. **Thread Life Cycle Demonstration**:
   - Created in `@PostConstruct` (`NEW`).
   - Started via `daemon.start()` (`RUNNABLE`).
   - Periodically pauses using `Thread.sleep(interval)` (`TIMED_WAITING`).
   - Safely interrupted and joined during `@PreDestroy` (`TERMINATED`).
3. **Thread Safety**: Uses `Collections.synchronizedSet` and `ConcurrentLinkedQueue` to guarantee that alerts are dispatched safely across concurrent threads without duplicate notification spam.

---

## 5. Java I/O Streams (Character vs. Byte Streams)

### Decision:
Separate character and byte streams are deployed depending on the data type:
1. **Character Streams (`PrintWriter`, `BufferedWriter`, `StringWriter`)**:
   - Employed in `CharacterStreamReportExporter` for human-readable academic project dossiers. Character streams natively handle Unicode character encoding without byte corruption.
2. **Byte Streams (`ByteArrayOutputStream`, `BufferedOutputStream`, `OutputStreamWriter`)**:
   - Employed in `ByteStreamDataExporter` for binary streaming of CSV files across the HTTP servlet response.
3. **Character Stream Ingestion (`BufferedReader`, `FileReader`)**:
   - Employed in `SkillCatalogCsvImporter` for parsing the line-delimited initial skills taxonomy.
