# 08 – Configuration Specification

## Overview
All runtime parameters for the Telecom Tower Maintenance Portal are managed through Ansible variables. There is a single source of truth per environment layer, following Ansible's variable precedence (lowest → highest):

```
role defaults → group_vars → host_vars → extra-vars (-e)
```

---

## Variable Layers

### `ansible/roles/tower_portal/defaults/main.yml` (lowest priority)
| Variable | Default | Description |
|---|---|---|
| `docker_restart_policy` | `unless-stopped` | Container restart policy |
| `release_history_file` | `/opt/tower-portal/releases/history.txt` | Deployment audit log path |

### `ansible/group_vars/all.yml` (primary configuration)
| Variable | Value | Description |
|---|---|---|
| `app_name` | `tower-portal` | Logical application name |
| `app_user` | `appuser` | OS user for directory ownership |
| `app_group` | `appuser` | OS group for directory ownership |
| `app_base_dir` | `/opt/tower-portal` | Root directory on target host |
| `docker_image` | `vedanti17/telecom-tower-portal` | Docker Hub image name |
| `docker_tag` | `latest` | Image tag (override with `-e docker_tag=1.0.7`) |
| `container_name` | `tower-portal-ansible` | Docker container name |
| `host_port` | `8085` | Host-side port exposed |
| `container_port` | `8080` | Internal container port |
| `spring_profile` | `prod` | Active Spring profile |
| `db_url` | `jdbc:h2:file:./data/telecomdb;DB_CLOSE_DELAY=-1` | H2 file DB (relative to `/app`) |
| `db_driver` | `org.h2.Driver` | JDBC driver class |
| `db_username` | `sa` | Database username |
| `db_password` | *(empty)* | Database password |
| `healthcheck_url` | `http://localhost:8085/actuator/health` | Health endpoint |
| `healthcheck_retries` | `12` | Max poll attempts |
| `healthcheck_delay` | `5` | Seconds between retries |

---

## Environment File (`/opt/tower-portal/config/app.env`)
Generated from `roles/tower_portal/templates/app.env.j2` on every playbook run. Passed to the container via `--env-file`. Contains:

```
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:h2:file:./data/telecomdb;DB_CLOSE_DELAY=-1
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=
SERVER_PORT=8080
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics
MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=always
```

---

## Directory Structure on Target (`/opt/tower-portal/`)
```
/opt/tower-portal/
├── config/
│   └── app.env          ← rendered env file (owner: UID 999)
├── data/                ← H2 database files (mounted to /app/data)
│   ├── telecomdb.mv.db
│   └── telecomdb.trace.db
├── logs/                ← application logs (mounted to /app/logs)
└── releases/
    └── history.txt      ← deployment audit trail
```

> **Note:** All directories are owned by UID/GID **999** (the container's `appuser`), not the host's `appuser` (UID 1001), to avoid file permission errors with H2 file-based storage on mounted volumes.

---

## Port Allocation
| Port | Service |
|------|---------|
| 8080 | Tomcat (host) |
| 8081 | Jenkins |
| 8082 | Dev container (Jenkins CD) |
| 8083 | Test container (Jenkins CD) |
| **8085** | **Ansible-managed container (Task 13/14)** |

---

## Overriding Variables at Runtime
```bash
# Deploy a specific versioned tag
ansible-playbook playbook.yml -e "docker_tag=1.0.7"

# Use a different host port
ansible-playbook playbook.yml -e "host_port=8086"

# Teardown and delete all data
ansible-playbook teardown.yml -e "remove_data=true"
```
