# Project Execution & Verification Evidence Log

**Project:** Telecom Tower Maintenance Portal  
**Target:** Tasks 1 to 10 Execution Evidence  

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


---

## Task 9: Selenium E2E Test Suite

- **Status:** COMPLETED
- **Branch:** `feature/selenium-tests` → merged to `develop` via [PR #14](https://github.com/Vedanti1703/devops/pull/14)
- **Deliverables:**
  - `src/test/java/com/telecom/tower/selenium/BaseSeleniumTest.java` — headless Chrome, `@SpringBootTest(RANDOM_PORT)`, helpers `login()` and `baseUrl()`
  - `src/test/java/com/telecom/tower/selenium/ScreenshotOnFailureExtension.java` — JUnit 5 `AfterTestExecutionCallback` saves PNG to `target/screenshots/<testName>.png` before `@AfterEach` quits the driver
  - `LoginSeleniumTest.java`, `CreateRecordSeleniumTest.java`, `SearchSeleniumTest.java`, `WorkflowDashboardSeleniumTest.java`
- **Selenium dependency added to pom.xml (test scope):**
  ```xml
  <dependency>
      <groupId>org.seleniumhq.selenium</groupId>
      <artifactId>selenium-java</artifactId>
      <version>4.x</version>
      <scope>test</scope>
  </dependency>
  ```
- **Commands Executed:**
  ```powershell
  git checkout develop
  git checkout -b feature/selenium-tests
  # ... code authoring ...
  mvn clean test
  git add -A
  git commit -m "test(selenium): add Selenium E2E tests for Task 9"
  git push origin feature/selenium-tests
  gh pr create --base develop --head feature/selenium-tests --title "Task 9: Selenium E2E Tests"
  gh pr merge 14 --squash   # (series of fix commits then merge)
  ```
- **Fix Commits (in branch, squashed on merge):**
  | Commit | Message |
  |--------|---------|
  | `c8d86de` | `test(selenium): add Selenium E2E tests for Task 9` |
  | `40d5531` | `fix(selenium): screenshot extension fires before driver quit` |
  | `cc2e8b9` | `fix(selenium): robust form-wait and in-memory H2 for tests` |
  | `940d472` | `fix(selenium): reliable form submit and input filling via JS in headless Chrome` |
  | `8f0e65b` | Merge commit — PR #14 into `develop` |
- **Final Test Result:** 21 tests pass, 0 failures, 0 errors (`BUILD SUCCESS`)
- **Key Bugs Fixed:**
  1. Screenshot extension reordered to `AfterTestExecutionCallback` (runs before `@AfterEach` driver quit)
  2. Form submit switched to JS executor for reliable headless Chrome interaction
  3. Test database isolated with in-memory H2 profile so Tomcat data never leaks
- **Manual Screenshots Required:**
  - Jenkins / terminal screenshot of `mvn clean test` → **21 tests, 0 failures** summary line
  - Example screenshot from `target/screenshots/` (any failure PNG captured during development)

---

## Task 10: Continuous Testing in Jenkins

- **Status:** COMPLETED
- **Documentation:** [`docs/05-continuous-testing.md`](file:///c:/Users/Vedanti/OneDrive/Desktop/devops%20project/docs/05-continuous-testing.md)

### Part 1 — Jenkinsfile Test Stage (PR #15)

- **Branch:** `feature/jenkins-testing` → merged to `develop` via [PR #15](https://github.com/Vedanti1703/devops/pull/15)
- **Commits:**
  | Commit | Message |
  |--------|---------|
  | `44fd6af` | `ci: add selenium test stage to Jenkinsfile` |
  | `1c71c3b` | `docs: add continuous testing documentation` |
  | `9ced854` | Merge commit — PR #15 into `develop` |
- **Jenkinsfile change added (Test stage):**
  ```groovy
  stage('Test') {
      steps {
          bat 'mvn test'
      }
      post {
          always {
              junit 'target/surefire-reports/*.xml'
              archiveArtifacts artifacts: 'target/screenshots/*.png', allowEmptyArchive: true
          }
      }
  }
  ```
- **Commands Executed:**
  ```powershell
  git checkout develop; git checkout -b feature/jenkins-testing
  # edited Jenkinsfile
  git add Jenkinsfile docs/05-continuous-testing.md
  git commit -m "ci: add selenium test stage to Jenkinsfile"
  git push origin feature/jenkins-testing
  gh pr create --base develop --head feature/jenkins-testing --title "Task 10 Part 1: CI Test Stage"
  gh pr merge 15 --merge
  git checkout develop; git pull
  ```
- **Pipeline Run 1 — GREEN ✅:** All 21 tests passed; Package and Deploy ran successfully; WAR deployed to Tomcat.

### Part 2 — Deliberate Defect (PR #16)

- **Branch:** `bugfix/intentional-defect` → merged to `develop` via [PR #16](https://github.com/Vedanti1703/devops/pull/16)
- **Commits:**
  | Commit | Message |
  |--------|---------|
  | `8f573c2` | `test: introduce deliberate defect for pipeline demo` |
  | `2f5562b` | Merge commit — PR #16 into `develop` |
- **Defect Introduced:** `dashboard.html` heading changed from `Telecom Operations KPI Dashboard` → `Overview`, causing `WorkflowDashboardSeleniumTest.dashboard_isAccessibleAndShowsStats` (TC-W-02) to fail.
- **Pipeline Run 2 — RED ❌:** Test stage FAILED → Package and Deploy stages **SKIPPED** (pipeline gating confirmed; deployment blocked).

### Part 3 — Defect Fix & Redeployment (PR #17)

- **Branch:** `bugfix/fix-defect` → merged to `develop` via [PR #17](https://github.com/Vedanti1703/devops/pull/17)
- **Commits:**
  | Commit | Message |
  |--------|---------|
  | `9048dd0` | `fix: correct defect found by Selenium tests` |
  | `3e1f165` | Merge commit — PR #17 into `develop` |
- **Fix Applied:** Heading in `dashboard.html` restored to `Telecom Operations KPI Dashboard`.
- **Pipeline Run 3 — GREEN ✅:** All 21 tests pass; Package and Deploy ran; WAR redeployed to Tomcat.

### Git Log — `develop` branch (Task 9 + 10 range)

```
*   3e1f165 Merge pull request #17 from Vedanti1703/bugfix/fix-defect
|\
| * 9048dd0 fix: correct defect found by Selenium tests
|/
*   2f5562b Merge pull request #16 from Vedanti1703/bugfix/intentional-defect
|\
| * 8f573c2 test: introduce deliberate defect for pipeline demo
|/
*   9ced854 Merge pull request #15 from Vedanti1703/feature/jenkins-testing
|\
| * 1c71c3b docs: add continuous testing documentation
| * 44fd6af ci: add selenium test stage to Jenkinsfile
|/
*   8f0e65b Merge pull request #14 from Vedanti1703/feature/selenium-tests
|\
| * 940d472 fix(selenium): reliable form submit and input filling via JS in headless Chrome
| * cc2e8b9 fix(selenium): robust form-wait and in-memory H2 for tests
| * 40d5531 fix(selenium): screenshot extension fires before driver quit
| * c8d86de test(selenium): add Selenium E2E tests for Task 9
|/
```

### 3 Pipeline Runs Summary

| Run # | Status | Trigger (PR) | Merge Commit | Description |
|:---:|:---:|:---:|:---:|---|
| 1 | ✅ GREEN | PR #15 | `9ced854` | Baseline: 21 tests pass, full pipeline executes, WAR deployed |
| 2 | ❌ RED | PR #16 | `2f5562b` | Deliberate defect: `TC-W-02` fails, Package & Deploy **blocked** |
| 3 | ✅ GREEN | PR #17 | `3e1f165` | Fix applied: 21 tests pass again, WAR redeployed to Tomcat |

### Screenshots Still Required

| # | Screenshot | Where to Capture |
|:---:|---|---|
| 1 | Jenkins **Run 1 (GREEN)** — pipeline stage view (Test ✅, Package ✅, Deploy ✅) | Jenkins → `tower-portal-pipeline` → Build #N |
| 2 | Jenkins **Run 2 (RED)** — Test stage RED, Package & Deploy SKIPPED | Jenkins → `tower-portal-pipeline` → Build #N+1 |
| 3 | Jenkins **Run 3 (GREEN)** — all stages green after fix | Jenkins → `tower-portal-pipeline` → Build #N+2 |
| 4 | Jenkins **JUnit test report** — 21 tests, 0 failures graph | Jenkins → Build → Test Result |
| 5 | Jenkins **archived screenshots** — `target/screenshots/*.png` artifacts panel | Jenkins → Build → Artifacts |
| 6 | Terminal — `mvn clean test` output with `Tests run: 21, Failures: 0, Errors: 0` | PowerShell locally |
| 7 | GitHub PR #15 merged — Jenkinsfile Test stage addition | https://github.com/Vedanti1703/devops/pull/15 |
| 8 | GitHub PR #16 merged — deliberate defect | https://github.com/Vedanti1703/devops/pull/16 |
| 9 | GitHub PR #17 merged — defect fix | https://github.com/Vedanti1703/devops/pull/17 |

