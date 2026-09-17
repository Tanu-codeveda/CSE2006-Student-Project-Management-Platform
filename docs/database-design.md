# Database Design & Storage Architecture

## 1. Entity-Relationship (ER) Diagram

```mermaid
erDiagram
    USERS ||--|| STUDENTS : "inherits via PK"
    USERS ||--|| FACULTY : "inherits via PK"
    USERS ||--|| ADMINISTRATORS : "inherits via PK"

    STUDENTS ||--o{ STUDENT_SKILLS : "possesses"
    SKILLS ||--o{ STUDENT_SKILLS : "classified as"

    STUDENTS ||--o{ PROJECTS : "creates as Lead"
    FACULTY ||--o{ PROJECTS : "supervises as Mentor"

    PROJECTS ||--|| TEAMS : "has exactly one"
    TEAMS ||--|{ TEAM_MEMBERS : "contains"
    STUDENTS ||--o{ TEAM_MEMBERS : "participates in"

    PROJECTS ||--o{ PROJECT_SKILLS : "requires"
    SKILLS ||--o{ PROJECT_SKILLS : "referenced in"

    PROJECTS ||--o{ TASKS : "contains"
    STUDENTS ||--o{ TASKS : "assigned to"

    PROJECTS ||--o{ MILESTONES : "tracks"
    PROJECTS ||--o{ ACTIVITY_LOGS : "records"
    USERS ||--o{ NOTIFICATIONS : "receives"

    USERS {
        bigint id PK
        varchar name
        varchar email UK
        varchar password
        varchar role
        timestamp created_at
        boolean active
    }

    STUDENTS {
        bigint user_id PK,FK
        varchar registration_number UK
        varchar department
        int year_of_study
        varchar bio
        int max_active_projects
    }

    FACULTY {
        bigint user_id PK,FK
        varchar employee_id UK
        varchar department
        varchar designation
    }

    SKILLS {
        bigint id PK
        varchar name UK
        varchar category
        varchar description
    }

    STUDENT_SKILLS {
        bigint id PK
        bigint student_id FK
        bigint skill_id FK
        varchar proficiency
        double years_experience
    }

    PROJECTS {
        bigint id PK
        varchar title
        text description
        bigint owner_id FK
        bigint mentor_id FK
        varchar status
        int team_size_limit
        varchar tech_stack
        date deadline
        timestamp created_at
        timestamp updated_at
    }

    TEAMS {
        bigint id PK
        bigint project_id FK,UK
        int max_members
        bigint version
    }

    TEAM_MEMBERS {
        bigint id PK
        bigint team_id FK
        bigint student_id FK
        varchar role_in_team
        timestamp joined_at
    }

    TASKS {
        bigint id PK
        varchar title
        text description
        bigint project_id FK
        bigint assignee_id FK
        varchar priority
        varchar status
        date deadline
        timestamp completed_at
        timestamp created_at
        bigint version
    }

    MILESTONES {
        bigint id PK
        varchar title
        text description
        bigint project_id FK
        date target_deadline
        boolean completed
        timestamp completion_date
        double weight_percentage
    }
```

---

## 2. Table Specifications & Constraints

### 1. `users` & Subtype Tables (`InheritanceType.JOINED`)
- `users`: Base polymorphic table. Stores credentials and role discriminator.
- `students`: Contains university-specific academic identifiers (`registration_number` unique).
- `faculty`: Contains faculty identifiers (`employee_id` unique).

### 2. `skills` & `student_skills`
- `skills`: Catalog of extensible technologies. Unique constraint on `name`.
- `student_skills`: Associative join table linking student to skill with `proficiency` (`BEGINNER`, `INTERMEDIATE`, `ADVANCED`, `EXPERT`) and years of experience. Composite uniqueness on `(student_id, skill_id)`.

### 3. `projects` & `project_skills`
- `projects`: Primary academic project entity. Status managed via `ProjectStatus` enum.
- `project_skills`: Defines minimum required proficiency and weight for rule-based matching. Composite uniqueness on `(project_id, skill_id)`.

### 4. `teams` & `team_members`
- `teams`: 1-to-1 relationship with `projects`. Maintains `@Version` column for optimistic locking in addition to synchronized slot logic.
- `team_members`: Links student to project team with custom roles (`Project Lead`, `Backend Architect`, `Frontend Lead`). Composite uniqueness on `(team_id, student_id)` prevents duplicate enrollment.

### 5. `tasks` & `milestones`
- `tasks`: Work breakdown tasks with priority severity ordering (`CRITICAL` > `HIGH` > `MEDIUM` > `LOW`) and status constraints.
- `milestones`: Structured evaluation roadmap with percentage weighting ($0-100\%$).
