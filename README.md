# Telecom Tower Maintenance Portal

A **role-based web application** for managing telecom tower field maintenance operations — built with **Java 17, Spring Boot 3, Thymeleaf, Spring Security, and H2/MySQL**.

---

## 🗼 Overview
Telecom companies operate hundreds of geographically distributed cell towers. Tracking maintenance activities via paper logbooks, Excel spreadsheets, or WhatsApp groups leads to delays, data loss, and compliance gaps.

This portal provides a **centralized, auditable, and role-governed** maintenance tracking system where:
- **Technicians** create and progress repair tickets.
- **Supervisors** verify or reject completed work.
- **Administrators** close verified records and manage the full system.

---

## ⚙️ Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 |
| View Engine | Thymeleaf 3 + Bootstrap 5 (CDN) |
| Persistence | Spring Data JPA + Hibernate |
| Database | H2 (file-based, dev) / MySQL (production) |
| Security | Spring Security 6 (in-memory RBAC) |
| Build Tool | Apache Maven 3.9.6 + Maven Wrapper |
| Observability | Spring Boot Actuator (`/actuator/health`) |
| Packaging | WAR (deployable to Tomcat 10) |

---

## 🚀 How to Run Locally

### Prerequisites
- JDK 17+ installed (`java -version`)
- Maven 3.8+ or use the included Maven Wrapper (`./mvnw`)
- Git 2.30+

### Steps
```bash
# 1. Clone the repository
git clone https://github.com/Vedanti1703/devops.git
cd devops

# 2. Run the application (uses embedded Tomcat on port 8081)
mvn spring-boot:run
# OR use the Maven Wrapper
./mvnw spring-boot:run

# 3. Open in browser
http://localhost:8081
```

### Demo Login Accounts

| Username | Password | Role |
|---|---|---|
| `tech1` | `tech123` | TECHNICIAN |
| `sup1` | `sup123` | SUPERVISOR |
| `admin` | `admin123` | ADMIN |

### Health Check
```
http://localhost:8081/actuator/health
```

---

## 📁 Folder Structure

```
devops/
├── docs/                          # Project documentation
│   ├── 01-problem-definition.md  # Problem statement and scope
│   ├── 02-agile-plan.md          # Agile backlog, Kanban, sprints
│   ├── 03-architecture.md        # SRS, architecture diagrams, endpoints
│   ├── branch-policy.md          # Git branching and commit strategy
│   ├── devops-lifecycle.md       # DevOps toolchain lifecycle diagram
│   └── evidence-log.md           # Task evidence and screenshots guide
├── src/
│   ├── main/
│   │   ├── java/com/telecom/tower/
│   │   │   └── TelecomTowerApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── templates/         # Thymeleaf HTML views
│   └── test/
│       └── java/com/telecom/tower/
│           └── TelecomTowerApplicationTests.java
├── .github/
│   ├── ISSUE_TEMPLATE/
│   │   ├── bug_report.md
│   │   └── feature_request.md
│   └── pull_request_template.md
├── .gitignore
├── mvnw / mvnw.cmd                # Maven Wrapper scripts
├── pom.xml                        # Maven build definition
└── README.md
```

---

## 🌿 Branch Policy

| Branch | Purpose |
|---|---|
| `main` | Stable, tagged releases only |
| `develop` | Integration branch for completed features |
| `feature/<name>` | Individual feature development |
| `bugfix/<name>` | Bug fix branches |
| `hotfix/<name>` | Critical production fix branches |

**All merges into `develop` or `main` require a Pull Request** — direct pushes are not permitted.

### Commit Message Convention
```
<type>: <short description>

Types: feat, fix, docs, chore, test, refactor, style
```

---

## 📋 DevOps Toolchain

| Phase | Tool |
|---|---|
| Plan | GitHub Issues & Projects |
| Code | Git + VS Code |
| Build | Apache Maven |
| Test | JUnit 5 + Selenium |
| Release | Docker + GitHub Packages |
| Deploy | Ansible |
| Operate | Apache Tomcat 10 |
| Monitor | Spring Boot Actuator |

---

## 📊 Project Documentation
See the `/docs` folder for detailed documentation:
- [Problem Definition](docs/01-problem-definition.md)
- [Agile Plan & Backlog](docs/02-agile-plan.md)
- [Architecture & API Reference](docs/03-architecture.md)
- [DevOps Lifecycle Diagram](docs/devops-lifecycle.md)
- [Branch Policy](docs/branch-policy.md)
