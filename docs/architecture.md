# System Architecture Document

## 1. Architectural Overview

The **Student Project & Team Management Platform (CoVe)** is engineered following a clean **N-Tier Layered Architecture** with strict separation of concerns:

1. **Presentation Layer (Web UI)**: Responsive HTML5/CSS3/JavaScript single-page and multi-page interfaces styled with university branding.
2. **REST Controller Layer**: Exposes contract-driven JSON and streaming HTTP endpoints, validating incoming requests and delegating to business services.
3. **Business Logic & Service Layer**: Encapsulates core domain rules:
   - Rule-based skill matching engine (method overloading, collections).
   - Team allocation service (thread-safe synchronization and capacity locks).
   - Project lifecycle service (state pattern and transition guards).
   - Task scheduling service (PriorityQueue ordering).
   - Milestone progress engine (weighted mathematical formula).
   - Multithreaded deadline monitoring daemon (extending `Thread`).
   - Java I/O reporting engine (character & byte streams).
4. **Data Access & Persistence Layer (Dual Persistence)**:
   - **Spring Data JPA / Hibernate**: Handles transactional CRUD and relational entity graphs.
   - **Direct JDBC (`java.sql.*`)**: `JdbcProjectAnalyticsDao` executes raw SQL queries via `PreparedStatement` and inspects `ResultSetMetaData` for analytical reporting.
5. **Database Layer**: Portable across **H2 In-Memory Database** (default for academic review) and **MySQL 8.0** (production profile).

---

## 2. System Architecture Diagram

```mermaid
flowchart TD
    subgraph Client_Layer["Client Tier (Web Browser)"]
        UI_Web["HTML5 / Modern CSS / Vanilla JS"]
    end

    subgraph Controller_Tier["REST Controller Tier (Spring Web MVC)"]
        ACtrl["AuthController"]
        PCtrl["ProjectController"]
        MCtrl["SkillMatchingController"]
        TCtrl["TeamController"]
        TaskCtrl["TaskController"]
        MileCtrl["MilestoneController"]
        NotifCtrl["NotificationController"]
        RepCtrl["ReportExportController"]
        AnCtrl["AnalyticsController"]
    end

    subgraph Business_Tier["Business Logic & Service Tier"]
        AServ["AuthService"]
        MServ["SkillMatchingService<br/>(Explainable Matcher)"]
        TServ["TeamManagementService<br/>(Synchronized Slots)"]
        PServ["ProjectService<br/>(State Transitions)"]
        TaskServ["TaskService<br/>(PriorityQueue Engine)"]
        MileServ["MilestoneService<br/>(Weighted Progress)"]
        NotifServ["NotificationService<br/>(Thread-Safe Queue)"]
        IOServ["Character & Byte Stream Exporters"]
        Daemon["DeadlineMonitoringDaemon<br/>(Worker Thread extends Thread)"]
    end

    subgraph Data_Access_Tier["Data Access Tier (Dual Persistence)"]
        JPA_Layer["Spring Data JPA / Hibernate ORM<br/>(Entities, Repositories, Cascades)"]
        JDBC_Layer["Direct JDBC DAO<br/>(Connection, PreparedStatement, ResultSetMetaData)"]
    end

    subgraph Storage_Tier["Database Storage Tier"]
        H2_DB[("Embedded H2 DB<br/>(Zero-Config Academic Mode)")]
        MySQL_DB[("MySQL 8.0 Server<br/>(Port 3306 Production Mode)")]
    end

    UI_Web <-->|REST / JSON & Stream Downloads| Controller_Tier
    Controller_Tier --> Business_Tier
    Daemon -.->|Proactive Alert Generation| NotifServ
    Business_Tier --> JPA_Layer
    Business_Tier --> JDBC_Layer
    JPA_Layer --> Storage_Tier
    JDBC_Layer --> Storage_Tier
```

---

## 3. Class Diagram & Inheritance Hierarchy

```mermaid
classDiagram
    class User {
        <<abstract>>
        -Long id
        -String name
        -String email
        -String password
        -Role role
        -LocalDateTime createdAt
        -boolean active
        +getRoleName()* String
        +getDashboardUrl()* String
        +canCreateProjects()* boolean
        +canAssignTasks()* boolean
    }

    class Student {
        -String registrationNumber
        -String department
        -int yearOfStudy
        -String bio
        -int maxActiveProjects
        -Set~StudentSkill~ skills
        +addSkill(Skill, ProficiencyLevel, double)
        +removeSkill(Skill)
        +getProficiencyFor(String) ProficiencyLevel
        +getRoleName() String
        +getDashboardUrl() String
    }

    class Faculty {
        -String employeeId
        -String department
        -String designation
        -String officeRoom
        +getRoleName() String
        +getDashboardUrl() String
    }

    class Administrator {
        -String adminCode
        -boolean superAdmin
        +getRoleName() String
        +getDashboardUrl() String
    }

    class Project {
        -Long id
        -String title
        -String description
        -ProjectStatus status
        -int teamSizeLimit
        -LocalDate deadline
        -Team team
        -Set~ProjectSkill~ requiredSkills
        -List~Task~ tasks
        -List~Milestone~ milestones
        +validateAndSetStatus(ProjectStatus)
        +calculateProgressPercentage() double
        +isOverdue() boolean
    }

    class Team {
        -Long id
        -int maxMembers
        -List~TeamMember~ members
        +synchronized addMemberSynchronized(Student, String) boolean
        +synchronized removeMemberSynchronized(Student) boolean
        +isFull() boolean
    }

    class Task {
        -Long id
        -String title
        -TaskPriority priority
        -TaskStatus status
        -LocalDate deadline
        +synchronized transitionTo(TaskStatus)
        +compareTo(Task) int
        +isOverdue() boolean
    }

    class DeadlineMonitoringDaemon {
        -long checkIntervalMs
        -boolean running
        +run()
        +executeAuditCycle()
        +getDaemonDiagnostics() Map
    }

    User <|-- Student : Inheritance
    User <|-- Faculty : Inheritance
    User <|-- Administrator : Inheritance
    Project "1" *-- "1" Team : Composition
    Project "1" *-- "*" Task : Aggregation
    Project "1" *-- "*" Milestone : Aggregation
    DeadlineMonitoringDaemon --|> Thread : Extends
```
