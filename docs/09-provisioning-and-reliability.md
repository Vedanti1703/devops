# 09 – Provisioning and Reliability Guide

## Overview
Task 13 delivers **idempotent, repeatable provisioning** of the Telecom Tower Maintenance Portal to any Ubuntu 22.04 target using Ansible. Task 14 delivers **automated reliability validation** via a standalone health-check playbook.

---

## Task 13 – Configuration Management with Ansible

### Architecture
```
Control Node (WSL Ubuntu-24.04)          Target Node (WSL Ubuntu-22.04)
─────────────────────────────────        ─────────────────────────────────
ansible-playbook playbook.yml   ──SSH──► Install Docker
                                         Create appuser (UID 999)
                                         Create /opt/tower-portal/{config,data,logs,releases}
                                         Write app.env from template
                                         Pull vedanti17/telecom-tower-portal:latest
                                         Start container (port 8085→8080)
                                         Poll /actuator/health until UP
                                         Append to releases/history.txt
```

### Playbook Run – Evidence

**Command:**
```bash
cd "/mnt/c/Users/Vedanti/OneDrive/Desktop/devops project/ansible"
ANSIBLE_CONFIG=ansible.cfg ansible-playbook -i inventory/hosts.ini playbook.yml
```

**Result (final run):**
```
PLAY RECAP
target : ok=20   changed=2   unreachable=0   failed=0   skipped=0   rescued=0   ignored=0
```

**Health check result:**
```json
{
  "status": "UP",
  "components": {
    "db":        { "status": "UP", "details": { "database": "H2" } },
    "diskSpace": { "status": "UP" },
    "ping":      { "status": "UP" }
  }
}
```

**Release history (`/opt/tower-portal/releases/history.txt`):**
```
2026-10-21T19:01:09Z  image=vedanti17/telecom-tower-portal:latest  container=tower-portal-ansible  host_port=8085
2026-10-21T19:07:21Z  image=vedanti17/telecom-tower-portal:latest  container=tower-portal-ansible  host_port=8085
2026-10-21T19:11:16Z  image=vedanti17/telecom-tower-portal:latest  container=tower-portal-ansible  host_port=8085
```

### Idempotency
Re-running `playbook.yml` on an already-provisioned host is safe:
- All install tasks report `ok` (no changes)
- Container is only recreated if the image or config changed
- Release history gets one new entry per run

---

## Task 14 – Reliability Validation

### Healthcheck Playbook
`healthcheck.yml` is a **standalone** playbook (no role, no become) that:
1. Polls `http://localhost:8085/actuator/health` until `status=UP` (max 6 retries × 5s)
2. Asserts `health.json.status == 'UP'` → fails the play if not
3. Runs `docker ps` and asserts the container shows `Up` in its status
4. Dumps the full health payload for audit

**Command:**
```bash
ANSIBLE_CONFIG=ansible.cfg ansible-playbook -i inventory/hosts.ini healthcheck.yml
```

**Result:**
```
TASK [Assert application status is UP]
ok: [target] => msg: ✅ Application is UP and healthy

TASK [Assert container is running]
ok: [target] => msg: ✅ Container tower-portal-ansible is running

PLAY RECAP
target : ok=5   changed=0   unreachable=0   failed=0
```

---

## Rollback Procedure

```bash
# Roll back to any previously-pushed image tag
ANSIBLE_CONFIG=ansible.cfg ansible-playbook -i inventory/hosts.ini rollback.yml \
  -e "rollback_tag=1.0.5"
```

The rollback playbook:
1. Pulls the specified tag from Docker Hub
2. Kills and recreates the container with the old image
3. Records the rollback in `releases/history.txt`
4. Polls health until UP before completing

---

## Teardown Procedure

```bash
# Remove container only (data preserved)
ANSIBLE_CONFIG=ansible.cfg ansible-playbook -i inventory/hosts.ini teardown.yml

# Remove container + all data
ANSIBLE_CONFIG=ansible.cfg ansible-playbook -i inventory/hosts.ini teardown.yml \
  -e "remove_data=true"
```

---

## Known Issues & Lessons Learned

| Issue | Root Cause | Fix Applied |
|-------|-----------|-------------|
| Port 8084 stuck after failed run | Docker leaves kernel port binding after failed container start | Switched to port 8085; added `docker rm -f` in teardown |
| H2 `AccessDeniedException` on volume mount | UID mismatch: host `appuser`=1001, container `appuser`=999 | All mounted dirs owned by numeric UID `999` |
| H2 `Could not open file` | Volume mounted to `/opt/tower-portal/data` but app writes to `/app/data` (relative path) | Fixed volume mount to `:/app/data` |
