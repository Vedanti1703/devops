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
