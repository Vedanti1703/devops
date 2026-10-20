# 03 - System Architecture & Requirements Specification

**Project:** Telecom Tower Maintenance Portal  
**Document:** Software Requirements Specification (SRS) & Architectural Design  
**Target Platform:** Java 17 / Spring Boot 3 / Apache Tomcat  

---

## 1. Software Requirements Specification (SRS) Summary

### 1.1 Functional Requirements (FR)
- **FR-01 (Authentication & RBAC):** The system shall authenticate users and enforce role-based access control with roles `ROLE_TECHNICIAN`, `ROLE_SUPERVISOR`, and `ROLE_ADMIN`.
- **FR-02 (Record Creation):** Technicians and Admins shall be able to create maintenance records with attributes: `towerId`, `location`, `issueType`, `description`, `priority` (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`), and `assignedTechnician`.
- **FR-03 (State Machine Enforcement):**
  - Technicians can transition: `OPEN` $\to$ `IN_PROGRESS` $\to$ `COMPLETED`.
  - Supervisors can transition: `COMPLETED` $\to$ `VERIFIED`, or `COMPLETED` $\to$ `IN_PROGRESS` (rejection).
  - Admins can transition: `VERIFIED` $\to$ `CLOSED`, and perform administrative overrides across any state.
  - Any invalid transition attempt shall be rejected with an informative error message.
- **FR-04 (Record Search & Filter):** Users shall be able to filter maintenance tickets by Tower ID, Location, Status, and Priority.
- **FR-05 (Summary Dashboard):** The system shall compute and display live summary metrics including: total records, count by status, count by priority, and pending vs. completed/verified counts.
- **FR-06 (Health & Observability):** The system shall expose a `/actuator/health` endpoint reporting operational status.

### 1.2 Non-Functional Requirements (NFR)
- **NFR-01 (Security):** Passwords securely checked, CSRF protection enabled on web forms, and role-based endpoint restriction.
- **NFR-02 (Performance):** Dashboard page rendering shall take $< 2$ seconds under normal local load.
- **NFR-03 (Packaging & Portability):** Packaged as a standard deployable Web Application Archive (WAR), compatible with external Apache Tomcat and embedded Tomcat execution.
- **NFR-04 (Testability):** All interactive input fields, action buttons, and status indicators shall carry static and deterministic HTML `id` attributes to enable automated Selenium browser testing.
- **NFR-05 (Reliability & Persistence):** Maintenance records are persisted to an H2 file-based database (`./data/telecomdb`), with simple schema compatibility for MySQL production switchover via `application.properties`.

---

## 2. Mermaid Use-Case Diagram

```mermaid
flowchart TD
    Tech["Field Technician\n(tech1)"]
    Sup["Maintenance Supervisor\n(sup1)"]
    Admin["System Administrator\n(admin)"]

    subgraph Portal ["Telecom Tower Maintenance Portal"]
        UC1((Log in to Portal))
        UC2((Create Maintenance Ticket))
        UC3((View Records List))
        UC4((View Record Details))
        UC5((Start Work: OPEN -> IN_PROGRESS))
        UC6((Complete Work: IN_PROGRESS -> COMPLETED))
        UC7((Approve: COMPLETED -> VERIFIED))
        UC8((Reject: COMPLETED -> IN_PROGRESS))
        UC9((Close: VERIFIED -> CLOSED))
        UC10((Filter & Search Records))
        UC11((View KPI Dashboard))
    end

    Tech --> UC1
    Tech --> UC2
    Tech --> UC3
    Tech --> UC4
    Tech --> UC5
    Tech --> UC6
    Tech --> UC10
    Tech --> UC11

    Sup --> UC1
    Sup --> UC3
    Sup --> UC4
    Sup --> UC7
    Sup --> UC8
    Sup --> UC10
    Sup --> UC11

    Admin --> UC1
    Admin --> UC2
    Admin --> UC3
    Admin --> UC4
    Admin --> UC5
    Admin --> UC6
    Admin --> UC7
    Admin --> UC8
    Admin --> UC9
    Admin --> UC10
    Admin --> UC11
```

---

## 3. Layered Architecture Diagram

```mermaid
flowchart TB
    Client["Web Browser / Selenium Automation"]

    subgraph Presentation ["Presentation Layer (Thymeleaf & Spring Security)"]
        SecFilter["Spring Security Filter Chain\n(Authentication & RBAC)"]
        DashboardCtrl["DashboardController"]
        RecordCtrl["MaintenanceRecordController"]
        ThymeleafViews["Thymeleaf Templates\n(Bootstrap 5 CDN + HTML5 IDs)"]
    end

    subgraph Business ["Business Logic Layer"]
        RecordService["MaintenanceRecordService\n(Validation & Workflow Transitions)"]
    end

    subgraph Data ["Data Access Layer (Spring Data JPA)"]
        RecordRepo["MaintenanceRecordRepository\n(JpaRepository + Custom Queries)"]
    end

    subgraph Storage ["Persistence Layer"]
        H2DB[("H2 File Database\n(telecomdb.mv.db)")]
        MySQL[("MySQL Database\n(Production Profile)")]
    end

    Client -->|HTTP GET/POST| SecFilter
    SecFilter --> DashboardCtrl
    SecFilter --> RecordCtrl
    DashboardCtrl --> RecordService
    RecordCtrl --> RecordService
    RecordService --> RecordRepo
    RecordRepo -->|JPA / Hibernate| H2DB
    RecordRepo -.->|JDBC Switch| MySQL
    DashboardCtrl --> ThymeleafViews
    RecordCtrl --> ThymeleafViews
    ThymeleafViews --> Client
```

---

## 4. Entity-Relationship (ER) Data Model

```mermaid
erDiagram
    MAINTENANCE_RECORD {
        BIGINT id PK "Auto-increment Identifier"
        VARCHAR tower_id "e.g. TWR-DEL-104"
        VARCHAR location "Physical location or GPS coordinate"
        VARCHAR issue_type "e.g. POWER_FAILURE, ANTENNA_TILT"
        TEXT description "Detailed problem description"
        VARCHAR priority "LOW, MEDIUM, HIGH, CRITICAL"
        VARCHAR status "OPEN, IN_PROGRESS, COMPLETED, VERIFIED, CLOSED"
        VARCHAR assigned_technician "Username of field tech"
        VARCHAR created_by "Username of creator"
        TIMESTAMP created_at "Record creation timestamp"
        TIMESTAMP updated_at "Last update timestamp"
    }

    USER_ACCOUNT {
        VARCHAR username PK "tech1, sup1, admin"
        VARCHAR password "Bcrypt or {noop} encrypted"
        VARCHAR role "TECHNICIAN, SUPERVISOR, ADMIN"
        VARCHAR full_name "Display Name"
    }

    USER_ACCOUNT ||--o{ MAINTENANCE_RECORD : creates_or_assigned
```

---

## 5. API & Web Endpoint Catalog

| Endpoint | HTTP Method | Authorized Roles | Description |
|---|:---:|---|---|
| `/login` | GET | PermitAll | Renders login page. |
| `/login` | POST | PermitAll | Processes authentication credentials. |
| `/logout` | GET/POST | Authenticated | Logs out current user and clears session. |
| `/` | GET | Authenticated | Redirects to `/dashboard`. |
| `/dashboard` | GET | Authenticated | Renders KPI summary cards and breakdown tables. |
| `/records` | GET | Authenticated | Displays records list with search filters. |
| `/records/new` | GET | TECHNICIAN, ADMIN | Displays ticket creation form. |
| `/records` | POST | TECHNICIAN, ADMIN | Validates and persists new maintenance record. |
| `/records/{id}` | GET | Authenticated | Displays detailed record view and transition controls. |
| `/records/{id}/edit` | GET | ADMIN | Displays edit form for all fields. |
| `/records/{id}/update`| POST | ADMIN | Persists full record updates. |
| `/records/{id}/transition` | POST | Authenticated | Executes role-validated status transition. |
| `/actuator/health` | GET | PermitAll | Spring Boot Actuator health check endpoint. |

---

## 6. Technology Choices & Justification

| Component | Choice | Justification |
|---|---|---|
| **Language & SDK** | Java 17 LTS | Modern LTS Java release offering performance, strong typing, record types, and long-term stability. |
| **Framework** | Spring Boot 3.2.x | Enterprise standard framework providing rapid configuration, Actuator health endpoints, and embedded/WAR deployment flexibility. |
| **View Engine** | Thymeleaf 3 | Server-side HTML5 template engine seamlessly integrated with Spring MVC, eliminating heavy frontend build steps while producing clean DOM elements for Selenium. |
| **Persistence** | Spring Data JPA + Hibernate | Simplifies CRUD operations, supports dynamic query methods, and facilitates zero-code query generation. |
| **Database** | H2 (File-based) | Zero-configuration local database that persists across restarts and allows instant portability, with straightforward configuration migration to MySQL for production. |
| **Security** | Spring Security 6 | Industry-standard security architecture providing declarative role checks, password hashing, and CSRF protection. |
| **UI Styling** | Bootstrap 5.3 (CDN) | Lightweight, responsive, professional layout requiring no npm toolchains. |
| **Packaging** | WAR (Maven Wrapper) | Meets the deployment requirement for Tomcat 10 containerization while remaining runnable via `mvn spring-boot:run`. |

---

## 7. Local Setup Instructions

### Prerequisites
- Java Development Kit (JDK) 17 or higher (`java -version`).
- Apache Maven 3.8+ or Maven Wrapper (`./mvnw`).
- Git 2.30+.

### Steps to Run
1. Clone the repository:
   ```bash
   git clone <REPO_URL>
   cd telecom-tower-maintenance-portal
   ```
2. Build and verify test suite:
   ```bash
   mvn clean test
   ```
3. Run the application locally:
   ```bash
   mvn spring-boot:run
   ```
4. Access the web application:
   - URL: `http://localhost:8081` (Port 8081 is chosen as Jenkins standardly occupies 8080)
   - Login credentials:
     - Technician: `tech1` / `tech123`
     - Supervisor: `sup1` / `sup123`
     - Administrator: `admin` / `admin123`
5. Verify health check:
   - URL: `http://localhost:8081/actuator/health`
