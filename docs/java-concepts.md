# CSE2006 Java Syllabus Concept Mapping

This document provides a comprehensive mapping between the **VIT Bhopal CSE2006 (Programming in Java)** course syllabus and the concrete implementations in the **Student Project & Team Management Platform (CoVe)**.

---

## Syllabus Topic to Implementation Mapping

### Unit 1: Java Introduction & Flow Control
| Concept | Concrete Implementation | Academic Justification |
| :--- | :--- | :--- |
| **Java Variables & Data Types** | Primitive types (`int`, `long`, `double`, `boolean`) and reference types across all model classes (`Project.java`, `Student.java`, `Task.java`). | Essential for representing entities, IDs, ratings, and timestamps. |
| **Flow Control (`if...else`)** | `SkillMatchingService.java` (lines 90–120), `ProjectStatus.java` (lines 35–55). | Evaluates proficiency criteria, eligibility thresholds, and valid state transitions. |
| **`switch` Statement** | `AuthService.java` (lines 45–70), `ProjectStatus.java` (lines 40–55). | Handles role-specific polymorphic entity creation and state-machine transitions. |
| **`for` and `for-each` Loops** | `SkillMatchingService.java` (lines 55–70), `CharacterStreamReportExporter.java` (lines 65–110). | Iterates through candidate students, project skills, milestones, and task matrices. |
| **`break` and `continue`** | `DeadlineMonitoringDaemon.java` (line 85), `SkillMatchingService.java` (line 62). | Exits thread loops gracefully upon interrupt; skips ineligible students during candidate matching. |

---

### Unit 2: Object-Oriented Programming
| Concept | Concrete Implementation | Academic Justification |
| :--- | :--- | :--- |
| **Class & Objects (Exp 2)** | Defined across 15+ domain models (`User`, `Student`, `Faculty`, `Project`, `Team`, `Task`, `Milestone`). | Clean object-oriented domain modeling of the university project ecosystem. |
| **Constructors (Exp 1)** | Default, parameterized, and overloaded constructors in all entities. | Guarantees entity integrity and required initial states. |
| **Constructor Chaining (`this`)** | `User.java` (lines 45–55), `Task.java` (lines 65–75), `Student.java` (lines 55–65). | Avoids code duplication when initializing entities with varying parameters. |
| **Base Class Constructor (`super`, Exp 7)**| `Student.java` (`super(name, email, password, Role.STUDENT);`), `Faculty.java`, `Administrator.java`. | Ensures base class properties (`User`) are initialized correctly by derived subclasses. |
| **Encapsulation & Access Modifiers** | Private instance variables with public/protected getters and setters across all entities. | Protects invariants (e.g. team capacity, state transitions). |
| **Inheritance (Exp 5)** | `User` (abstract) $\rightarrow$ `Student`, `Faculty`, `Administrator`. | Establishes a clear "IS-A" hierarchy for university roles with shared identity attributes. |
| **Method Overriding (Exp 6, 8)** | `@Override public String getRoleName()` and `getDashboardUrl()` in `Student.java`, `Faculty.java`, `Administrator.java`. | Implements role-specific behavior and routing polymorphically. |
| **Run-time Polymorphism / Dynamic Dispatch (Exp 9)** | Invocation of `user.getDashboardUrl()` in `AuthService.java` and REST responses. | Resolves appropriate destination dashboard dynamically based on runtime subtype. |
| **Method Overloading (Exp 3, 4)** | `SkillMatchingService.java`: `evaluateStudent(student, project)`, `evaluateStudent(student, project, strict)`, `findMatchingCandidates(projectId)`, `findMatchingCandidates(projectId, threshold)`. | Supports compile-time polymorphism for matching with default and customized thresholds. |
| **Abstract Classes & Methods (Exp 11)**| `User.java` with abstract methods `getRoleName()`, `getDashboardUrl()`, `canCreateProjects()`, `canAssignTasks()`. | Defines contracts that every user role must satisfy without allowing direct instantiation of base `User`. |
| **Interfaces (Exp 12)** | `Comparable<Task>`, `Comparable<Skill>`, `Comparable<SkillMatchResponse>`, `CommandLineRunner`. | Enforces ordering contracts for collections sorting and priority scheduling. |
| **Enums with Constructors & Methods** | `ProficiencyLevel.java` (fields: rank, scoreWeight; method: `satisfies(required)`), `ProjectStatus.java`, `TaskPriority.java`. | Encapsulates domain constants and state validation logic cleanly. |

---

### Unit 3: Exception Handling & Multithreading
| Concept | Concrete Implementation | Academic Justification |
| :--- | :--- | :--- |
| **Custom Exception Hierarchy** | `ProjectManagementException` (base) extended by `TeamFullException`, `ProjectNotFoundException`, `InvalidTaskStateException`, `UnauthorizedActionException`. | Expresses domain-specific error conditions clearly without generic runtime crashes. |
| **`try-catch-finally`, `throw`, `throws`** | `GlobalExceptionHandler.java`, `TeamManagementService.java`, `DeadlineMonitoringDaemon.java`. | Intercepts errors cleanly and returns structured JSON responses with HTTP status codes. |
| **Multithreading by Extending `Thread` (Exp 13)** | `DeadlineMonitoringDaemon.java` extends `Thread`. | Performs proactive deadline checking in a continuous background loop without blocking HTTP request threads. |
| **Thread Life Cycle & Methods** | `DeadlineMonitoringDaemon.java`: `NEW` ($\rightarrow$) `RUNNABLE` (`start()`) $\rightarrow$ `TIMED_WAITING` (`Thread.sleep()`) $\rightarrow$ `TERMINATED` (`interrupt()`, `join()`). | Demonstrates complete thread lifecycle management and graceful shutdown. |
| **Synchronization** | `Team.java` (`public synchronized boolean addMemberSynchronized`), `TeamManagementService.java` (`teamLock.lock()`). | Eliminates race conditions when concurrent users attempt to claim the last open team slot. |

---

### Unit 4: Java Collections & I/O Streams
| Concept | Concrete Implementation | Academic Justification |
| :--- | :--- | :--- |
| **`List` / `ArrayList`** | `Project.java` (`List<Task>`, `List<Milestone>`), `Team.java` (`List<TeamMember>`). | Maintains ordered sequences of tasks and team rosters. |
| **`Set` / `HashSet`** | `Student.java` (`Set<StudentSkill>`), `Project.java` (`Set<ProjectSkill>`). | Enforces uniqueness of skills possessed or required. |
| **`PriorityQueue`** | `TaskService.java` (`getPrioritizedTasksForAssignee(studentId)`). | Dynamically sorts student tasks by severity level (`CRITICAL` first) and due date. |
| **Collections Sorting (`Comparable`)** | `Collections.sort(candidateRankings)` in `SkillMatchingService.java`. | Ranks candidates automatically by compatibility score. |
| **Character Streams (Exp 16)** | `CharacterStreamReportExporter.java` (`StringWriter`, `BufferedWriter`, `PrintWriter`). | Formats human-readable academic project dossiers without character corruption. |
| **Byte Streams (Exp 16)** | `ByteStreamDataExporter.java` (`ByteArrayOutputStream`, `BufferedOutputStream`, `OutputStreamWriter`). | Generates downloadable CSV binary streams for task and team rosters. |
| **Character Stream Ingestion (Exp 16)** | `SkillCatalogCsvImporter.java` (`BufferedReader`, `InputStreamReader`). | Reads and parses initial CSV skill catalog on startup. |

---

### Unit 5: Database Applications with JDBC & JPA
| Concept | Concrete Implementation | Academic Justification |
| :--- | :--- | :--- |
| **JPA / Hibernate Architecture** | `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Enumerated`, `@ManyToOne`, `@OneToMany`, `@OneToOne`. | Object-Relational Mapping simplifying domain persistence and relationship graphs. |
| **Direct JDBC API (Exp 14)** | `JdbcProjectAnalyticsDao.java` (`Connection`, `PreparedStatement`, `ResultSet`, `ResultSetMetaData`). | Executes raw aggregate SQL queries for cross-project skill demand and student workloads. |
| **External Driver Configuration** | `application.properties` and `application-mysql.properties` (`spring.datasource.url`, `driverClassName`). | Decouples database credentials and driver settings from Java source code. |
