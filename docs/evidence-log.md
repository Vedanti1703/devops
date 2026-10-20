# Project Execution & Verification Evidence Log

**Project:** Telecom Tower Maintenance Portal  
**Target:** Tasks 1 to 6 Execution Evidence  

---

## Task 1: Problem Definition and Scope
- **Status:** COMPLETED
- **Deliverables:** [`/docs/01-problem-definition.md`](file:///c:/Users/Vedanti/OneDrive/Desktop/devops%20project/docs/01-problem-definition.md)
- **Key Content:**
  - Defined problem statement regarding cell tower maintenance disorganization (paper/Excel/WhatsApp).
  - Identified target users (Field Technicians, Maintenance Supervisors, Operations Administrators).
  - Documented stakeholder expectations, technical constraints, and measurable success criteria.
  - Frozen 14-task MVP scope mapping table.
- **Manual Screenshot Required:**
  - Screenshot of `/docs/01-problem-definition.md` rendered preview or opened in editor.

---

## Task 2: Agile Planning and DevOps Workflow
- **Status:** COMPLETED
- **Deliverables:**
  - [`/docs/02-agile-plan.md`](file:///c:/Users/Vedanti/OneDrive/Desktop/devops%20project/docs/02-agile-plan.md)
  - [`/docs/devops-lifecycle.md`](file:///c:/Users/Vedanti/OneDrive/Desktop/devops%20project/docs/devops-lifecycle.md)
- **Key Content:**
  - 8 complete User Stories (US-01 through US-08) with Given/When/Then acceptance criteria.
  - Product backlog table with Fibonacci story points and sprint assignments.
  - 14-task Kanban board table.
  - 3-sprint plan with Mermaid Gantt timeline and Definition of Done (DoD) checklist.
  - DevOps lifecycle diagram and Jenkins CI/CD automation sequence diagram mapped to GitHub, Jenkins, Maven, Selenium, Docker, Ansible, and Tomcat.
- **Manual Screenshot Required:**
  - Screenshot of `/docs/02-agile-plan.md` and the Mermaid lifecycle flowchart in `/docs/devops-lifecycle.md`.

---

## Task 3: Requirements, Architecture and Local Setup
- **Status:** COMPLETED (Awaiting User Confirmation)
- **Deliverables:**
  - [`/docs/03-architecture.md`](file:///c:/Users/Vedanti/OneDrive/Desktop/devops%20project/docs/03-architecture.md)
  - [`pom.xml`](file:///c:/Users/Vedanti/OneDrive/Desktop/devops%20project/pom.xml)
  - [`src/main/resources/application.properties`](file:///c:/Users/Vedanti/OneDrive/Desktop/devops%20project/src/main/resources/application.properties)
  - [`src/main/java/com/telecom/tower/TelecomTowerApplication.java`](file:///c:/Users/Vedanti/OneDrive/Desktop/devops%20project/src/main/java/com/telecom/tower/TelecomTowerApplication.java)
  - Maven Wrapper (`./mvnw`, `mvnw.cmd`)
- **Commands Executed:**
  ```powershell
  mvn wrapper:wrapper
  mvn clean package
  mvn spring-boot:run
  curl.exe -i http://localhost:8081/actuator/health
  ```
- **Key Output:**
  ```json
  HTTP/1.1 200 OK
  {
    "status": "UP",
    "components": {
      "db": {
        "status": "UP",
        "details": { "database": "H2", "validationQuery": "isValid()" }
      },
      "diskSpace": { "status": "UP" },
      "ping": { "status": "UP" }
    }
  }
  ```
- **Manual Screenshot Required:**
  - Screenshot of terminal showing `BUILD SUCCESS` from `mvn clean package`.
  - Screenshot of browser navigating to `http://localhost:8081/actuator/health` displaying `{"status":"UP", ...}`.

---

## Task 4: Git and GitHub Repository Initialization
- **Status:** COMPLETED
- **GitHub Repository URL:** https://github.com/Vedanti1703/devops
- **Branches Configured and Pushed:**
  - `main`: https://github.com/Vedanti1703/devops/tree/main
  - `develop`: https://github.com/Vedanti1703/devops/tree/develop
- **Commits Created:**
  - `09e5382` docs: add problem definition, agile plan, and architecture
  - `d70949a` chore: add gitignore and readme
  - `702e90d` chore: add github issue and PR templates
  - `2a438c6` feat: initialize spring boot skeleton
- **GitHub Issues Created (8 user stories):**
  - [#1 US-01: User Authentication & Role-Based Access](https://github.com/Vedanti1703/devops/issues/1)
  - [#2 US-02: Maintenance Record Creation](https://github.com/Vedanti1703/devops/issues/2)
  - [#3 US-03: Maintenance Record Listing & Overview](https://github.com/Vedanti1703/devops/issues/3)
  - [#4 US-04: Record Detail & Inspection View](https://github.com/Vedanti1703/devops/issues/4)
  - [#5 US-05: Technician Progress Execution](https://github.com/Vedanti1703/devops/issues/5)
  - [#6 US-06: Supervisor Verification & Rejection Workflow](https://github.com/Vedanti1703/devops/issues/6)
  - [#7 US-07: Admin Final Closure & Oversight](https://github.com/Vedanti1703/devops/issues/7)
  - [#8 US-08: Executive Summary Dashboard & Search Filtering](https://github.com/Vedanti1703/devops/issues/8)
- **Manual Screenshot Required:**
  - Screenshot of GitHub repository page showing branches `main` and `develop`.
  - Screenshot of GitHub Issues tab showing all 8 issues created.

---

## Task 5: Feature Development with Branching
- **Status:** IN PROGRESS (PR #9 Created - Awaiting Review)
- **Branch Created:** `feature/create-view-records` (branched off `develop`)
- **Commits Created in Small Increments:**
  1. `96aaf23` feat: add maintenance record entity and enums
  2. `ac248d0` feat: add maintenance record spring data jpa repository
  3. `e4e0da9` feat: add maintenance service with state workflow and security config
  4. `ff98996` feat: add auth, dashboard, and maintenance record controllers
  5. `6f9f4bc` feat: add thymeleaf templates with bootstrap 5 and stable element ids
  6. `38d5cbb` test: add unit and integration test suite for maintenance records and state workflow
- **Pull Request Created:**
  - **PR #9:** https://github.com/Vedanti1703/devops/pull/9
  - Base: `develop` | Head: `feature/create-view-records`
  - Closes issues #1 through #8
- **Test Results:**
  - 14 automated tests run, 0 failures, 0 errors (**100% PASS**)
- **Manual Screenshot Required:**
  - Screenshot of terminal showing `git log --oneline --graph` with all feature commits.
  - Screenshot of Pull Request #9 on GitHub showing the description and checks.

---

## Task 6: MVP Completion and Git Collaboration
- **Status:** COMPLETED
- **Features Implemented & Verified:**
  - Full record update capability (`/records/{id}/edit`, `/records/{id}/update`)
  - Search & filter engine by Tower ID, Location, Status, and Priority
  - Role-based state transition workflow engine (Technician $\to$ Supervisor $\to$ Admin)
  - Executive KPI summary dashboard (`/dashboard`)
  - 14 automated unit and integration tests passing (`mvn clean test`)
- **Deliberate Merge Conflict Demonstration:**
  - Branch `feature/ui-title-change`: Navbar title changed to `"Telecom Tower Portal - Ops"` and merged to `develop` via PR #10.
  - Branch `feature/update-search-workflow-dashboard`: Navbar title changed to `"Tower Maintenance Portal - Dashboard"`.
  - Merged `develop` into `feature/update-search-workflow-dashboard`, triggering conflict on `src/main/resources/templates/layout.html`:
    ```html
    <<<<<<< HEAD
                    <span>Tower Maintenance Portal - Dashboard</span>
    =======
                    <span>Telecom Tower Portal - Ops</span>
    >>>>>>> develop
    ```
  - **Resolution Applied:** Unified brand title:
    ```html
                    <span>Telecom Tower Maintenance Portal</span>
    ```
  - Committed resolution: `fix(merge): resolve title conflict in layout.html between ops and dashboard branches`
- **Pull Requests Merged:**
  - **PR #10:** https://github.com/Vedanti1703/devops/pull/10 (`feature/ui-title-change` $\to$ `develop`)
  - **PR #11:** https://github.com/Vedanti1703/devops/pull/11 (`feature/update-search-workflow-dashboard` $\to$ `develop`)
  - **PR #12:** https://github.com/Vedanti1703/devops/pull/12 (`develop` $\to$ `main` release)
- **Release Tag:**
  - Annotated tag: `v1.0.0`
  - Tag message: `"MVP release baseline"`
  - Tag URL: https://github.com/Vedanti1703/devops/releases/tag/v1.0.0
- **Final Test Verification:**
  - Command: `mvn clean test`
  - Result: `BUILD SUCCESS` (14 tests passed, 0 failures, 0 errors)
- **Manual Screenshots Required:**
  - Screenshot of terminal showing the merge conflict markers in `layout.html`.
  - Screenshot of PR #11 and PR #12 merged on GitHub.
  - Screenshot of GitHub releases page showing tag `v1.0.0`.
  - Screenshot of Dashboard page (`http://localhost:8081/dashboard`) showing KPI cards.



