# DevOps Lifecycle & Toolchain Mapping

This document illustrates the end-to-end DevOps lifecycle for the **Telecom Tower Maintenance Portal**, mapping each phase from ideation to production observability against specific industry-standard tools.

---

## 1. DevOps Lifecycle Flowchart

```mermaid
flowchart LR
    subgraph Dev ["Development & Continuous Integration"]
        Plan["1. PLAN\n(GitHub Issues & Projects)"] --> Code["2. CODE\n(Git & VS Code)"]
        Code --> Build["3. BUILD\n(Maven Wrapper)"]
        Build --> Test["4. TEST\n(JUnit & Selenium)"]
    end

    subgraph Ops ["Release & Continuous Delivery"]
        Test --> Release["5. RELEASE\n(Docker & Nexus/GHCR)"]
        Release --> Deploy["6. DEPLOY\n(Ansible Playbooks)"]
        Deploy --> Operate["7. OPERATE\n(Apache Tomcat 10)"]
        Operate --> Monitor["8. MONITOR\n(Spring Boot Actuator)"]
    end

    Monitor -.->|"Feedback Loop"| Plan
```

---

## 2. Toolchain Mapping Matrix

| Lifecycle Phase | Tool | Role & Responsibility in Project |
|---|---|---|
| **1. Plan** | **GitHub Issues / Projects** | User stories, backlog grooming, bug tracking, sprint planning, and Kanban workflow. |
| **2. Code** | **Git / GitHub** | Feature-branch development (`feature/*`), code reviews via Pull Requests, and branch protection. |
| **3. Build** | **Apache Maven (`mvnw`)** | Automated dependency management, compilation, resource filtering, and WAR packaging. |
| **4. Test** | **JUnit 5 / Selenium** | Unit testing business rules/state transitions; automated browser testing with stable element IDs. |
| **5. Release** | **Docker / GitHub Packages** | Multi-stage Docker containerization of the WAR file and semantic release tagging (`v1.0.0`). |
| **6. Deploy** | **Ansible** | Automated configuration management, idempotently provisioning Tomcat servers and deploying WAR artifacts. |
| **7. Operate** | **Apache Tomcat** | Robust Java Servlet runtime container executing the Spring Boot WAR application. |
| **8. Monitor** | **Spring Boot Actuator** | Health check endpoints (`/actuator/health`), application metrics, and uptime surveillance. |

---

## 3. Automation Orchestration: Jenkins CI/CD Pipeline

```mermaid
sequenceDiagram
    autonumber
    actor Dev as Developer
    participant GH as GitHub Repository
    participant JNK as Jenkins CI Server
    participant MVN as Maven & JUnit
    participant SEL as Selenium Headless
    participant DKR as Docker Engine
    participant ANS as Ansible Controller
    participant TOM as Apache Tomcat Node

    Dev->>GH: Push commit to feature branch / PR
    GH->>JNK: Webhook triggers build pipeline
    JNK->>MVN: Run 'mvn clean compile test'
    MVN-->>JNK: Unit Tests PASS
    JNK->>MVN: Package WAR ('mvn package -DskipTests')
    JNK->>SEL: Execute automated UI integration tests
    SEL-->>JNK: UI Verification PASS
    JNK->>DKR: Build & Tag Container Image
    JNK->>ANS: Trigger deployment playbook
    ANS->>TOM: Deploy WAR & Restart Service
    TOM-->>JNK: Health check HTTP 200 OK
    JNK-->>Dev: Pipeline Succeeded Notification
```
