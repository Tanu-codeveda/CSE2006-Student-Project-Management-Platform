# System Workflows & Process Flows

## 1. Project Lifecycle State Transition Flow

Projects transition through a strict state machine implemented in `ProjectStatus.java`. Illegal transitions (such as transitioning directly from `IDEA` to `COMPLETED` or reviving an `ARCHIVED` project) are rejected with `InvalidProjectStateException`.

```mermaid
stateDiagram-v2
    [*] --> IDEA: Student Proposes Project
    IDEA --> OPEN: Requirements Defined & Published
    IDEA --> TEAM_FORMING: Immediate Recruitment
    IDEA --> ARCHIVED: Abandoned Concept

    OPEN --> TEAM_FORMING: Inquiries & Candidate Review
    OPEN --> IN_PROGRESS: Minimum Team Reached
    OPEN --> ARCHIVED: Term Cancelled

    TEAM_FORMING --> IN_PROGRESS: Final Team Confirmed
    TEAM_FORMING --> OPEN: Re-opened for Vacancies
    TEAM_FORMING --> ARCHIVED: Cancelled

    IN_PROGRESS --> COMPLETED: Milestones Met & Faculty Evaluated
    IN_PROGRESS --> ARCHIVED: Incomplete / Abandoned

    COMPLETED --> ARCHIVED: Academic Term Concluded
    ARCHIVED --> [*]: Terminal State
```

---

## 2. Rule-Based Skill Matching & Team Formation Sequence

This diagram details the sequence when a project lead reviews candidate students and enrolls a new teammate, demonstrating **thread-safe synchronization**:

```mermaid
sequenceDiagram
    autonumber
    actor Lead as Project Lead (Student)
    participant UI as Browser (Workspace)
    participant MatchCtrl as SkillMatchingController
    participant MatchServ as SkillMatchingService
    participant TeamServ as TeamManagementService
    participant Team as Team Entity (Synchronized)
    participant NotifServ as NotificationService
    actor Cand as Candidate Student

    Lead->>UI: Click "Rule-Based Skill Matcher" tab
    UI->>MatchCtrl: GET /api/projects/{id}/matches
    MatchCtrl->>MatchServ: findMatchingCandidates(projectId)
    MatchServ->>MatchServ: Iterate candidates & calculate weighted overlap
    MatchServ-->>MatchCtrl: List<SkillMatchResponse> (Sorted by Compatibility)
    MatchCtrl-->>UI: Render candidates with explainable badge breakdown
    
    Lead->>UI: Click "+ Add to Team" for Candidate
    UI->>TeamServ: POST /api/projects/{id}/team/join?studentId=candId
    TeamServ->>TeamServ: Acquire fair ReentrantLock (teamLock)
    TeamServ->>Team: addMemberSynchronized(cand, "Developer")
    
    alt Team has capacity (members < maxCapacity)
        Team-->>TeamServ: Success (TeamMember created)
        TeamServ->>NotifServ: sendNotification(Cand, "Enrolled in Project")
        TeamServ->>TeamServ: Release teamLock
        TeamServ-->>UI: 200 OK (TeamMember)
        UI-->>Lead: Toast "Candidate successfully added"
    else Team already full (race condition / concurrent claim)
        Team-->>TeamServ: throw TeamFullException(size, max)
        TeamServ->>TeamServ: Release teamLock
        TeamServ-->>UI: 409 Conflict (TeamFullException)
        UI-->>Lead: Error: "Team capacity reached"
    end
```

---

## 3. Multithreaded Background Deadline Daemon Workflow

```mermaid
flowchart TD
    StartDaemon([Start Daemon Thread]) --> InitState["Transition: NEW → RUNNABLE"]
    InitState --> RunLoop{"Is Running && Not Interrupted?"}
    
    RunLoop -- Yes --> QueryOverdue["Query Overdue Tasks<br/>(status ≠ COMPLETED && deadline < today)"]
    QueryOverdue --> Deduplicate["Check Alert Cache<br/>(Collections.synchronizedSet)"]
    Deduplicate --> DispatchAlerts["Dispatch Urgent Notification<br/>to Assignee & Project Lead"]
    
    DispatchAlerts --> QueryApproaching["Query Approaching Deadlines<br/>(deadline within 48h)"]
    QueryApproaching --> DispatchReminders["Dispatch Reminder Notifications"]
    
    DispatchReminders --> Sleep["Thread.sleep(30000)<br/>Transition: RUNNABLE → TIMED_WAITING"]
    Sleep --> RunLoop
    
    RunLoop -- Interrupted / Stopped --> Cleanup["Clean up resources & exit loop"]
    Cleanup --> Terminated(["Transition: TIMED_WAITING → TERMINATED"])
```
