# Problem Statement & System Scope

**Course:** CSE2006 – Programming in Java  
**Institution:** Vellore Institute of Technology (VIT Bhopal)  
**Evaluation:** VITyarthi - Build Your Own Project  
**Project Title:** Student Project & Team Management Platform (CoVe)

---

## 1. Problem Statement

At university campuses, students frequently face critical collaboration hurdles during academic course projects and capstone evaluations:

1. **Information Asymmetry in Teammate Discovery**: Students commonly resort to informal social channels (WhatsApp groups, verbal inquiries) to locate teammates, resulting in haphazard teams with overlapping duplicate skills and severe technical gaps.
2. **Lack of Transparent Compatibility**: Existing group formation lacks explainability; students cannot assess whether a potential teammate's proficiency matches specific project deliverables.
3. **Disorganized Task & Responsibility Allocation**: Project roles, action items, and task priorities remain fragmented across scattered chat messages, causing dropped deadlines and accountability confusion.
4. **Milestone & Progress Opacity**: Project progress is rarely tracked against formal academic milestones (Requirement Analysis, Architecture Design, Core Development, Testing, Final Presentation), leaving mentors unable to evaluate incremental progress.
5. **Absence of Proactive Deadline Monitoring**: Deadlines are frequently missed due to lack of proactive notifications and background monitoring.
6. **Concurrent Enrollment Conflicts**: Multiple students attempting to claim limited project vacancies simultaneously encounter race conditions and over-subscription when shared state is not synchronized.

---

## 2. Project Scope

**CoVe** is a full-stack, centralized academic platform developed from scratch to solve these challenges while rigorously demonstrating concepts from the CSE2006 Programming in Java syllabus:

- **In Scope**:
  - Role-based user management for Students, Faculty Mentors, and Platform Administrators.
  - Extensible, categorical technical skill catalog with 4-tier proficiency tracking (`BEGINNER`, `INTERMEDIATE`, `ADVANCED`, `EXPERT`).
  - Project lifecycle state machine (`IDEA`, `OPEN`, `TEAM_FORMING`, `IN_PROGRESS`, `COMPLETED`, `ARCHIVED`) with strict transition validation.
  - Transparent, explainable rule-based compatibility matching engine with itemized criteria checklists (`MATCH ✓`, `PARTIAL ⚠`, `MISSING ✗`).
  - Thread-safe, synchronized team slot allocation preventing race condition over-subscriptions.
  - Interactive Kanban task management board with priority scheduling (`PriorityQueue`).
  - Milestone roadmap with mathematically weighted completion calculation.
  - Continuous multithreaded background daemon thread (`DeadlineMonitoringDaemon`) proactively auditing overdue tasks and dispatching alerts.
  - Dual persistence architecture utilizing Spring Data JPA for domain entities and direct JDBC (`PreparedStatement`, `ResultSetMetaData`) for raw analytical queries.
  - Java Character and Byte I/O Streams generating formatted Project Dossiers and downloadable CSV datasets.

- **Out of Scope**:
  - Video conferencing or direct real-time audio chat (external communication tools handle video).
  - Commercial payment gateways (strictly non-commercial educational domain).
  - Black-box neural network machine learning (the matching engine is deliberately deterministic, auditable, and rule-based).

---

## 3. Target Users

| User Persona | Primary Needs & Workflow |
| :--- | :--- |
| **Student Project Lead** | Propose project ideas, define required competencies, recruit teammates using explainable matching scores, assign tasks, track milestone progress. |
| **Student Contributor** | Showcase verified skills and proficiency levels, discover open projects matching their skillset, claim tasks, track personal deadlines. |
| **Faculty Mentor** | Supervise course projects, guide task distribution, verify milestone completion, download academic project dossiers for grading. |
| **Academic Administrator** | Inspect platform-wide health metrics, monitor cross-project skill demand distributions, review student workload reports via direct JDBC analytics. |

---

## 4. High-Level System Features

```
┌─────────────────────────────────────────────────────────────────────────┐
│              Student Project & Team Management Platform (CoVe)           │
├─────────────────────────────────────────────────────────────────────────┤
│ 1. Multi-Role Identity & Profiles (Student, Faculty, Admin)            │
│ 2. Extensible Skills Taxonomy & Dynamic Profiling                       │
│ 3. Project Lifecycle State Engine (IDEA -> ARCHIVED)                    │
│ 4. Explainable Rule-Based Skill Compatibility Matcher                   │
│ 5. Concurrency-Protected Team Slot Allocation                           │
│ 6. Kanban Task Board & PriorityQueue Scheduling                         │
│ 7. Milestone Progress Engine with Mathematical Weighting                │
│ 8. Multithreaded Background Deadline Monitoring Daemon                  │
│ 9. Direct JDBC Analytical Aggregation Engine                            │
│ 10. Java I/O Character & Byte Stream Reporting (TXT & CSV Exporters)    │
│ 11. Centralized Custom Exception Handling Infrastructure                │
│ 12. Modern Responsive Web Interface                                    │
└─────────────────────────────────────────────────────────────────────────┘
```
