# Ansible – Telecom Tower Portal

## Overview
Idempotent provisioning and reliability management via Ansible for the **Telecom Tower Maintenance Portal**.

## Directory structure
```
ansible/
├── ansible.cfg                     # Ansible configuration
├── playbook.yml                    # Task 13 – main provision playbook
├── healthcheck.yml                 # Task 14 – standalone health validation
├── teardown.yml                    # Remove container (data preserved by default)
├── rollback.yml                    # Hot-swap to a previous image tag
├── inventory/
│   └── hosts.ini                   # Target node: Ubuntu-22.04 (port 2222)
├── group_vars/
│   └── all.yml                     # All tunable parameters
└── roles/
    └── tower_portal/
        ├── defaults/main.yml       # Lowest-priority role defaults
        ├── handlers/main.yml       # Container restart handler
        ├── tasks/main.yml          # 8-step idempotent provisioning
        └── templates/app.env.j2   # Spring Boot env file template
```

## Quick-start (from WSL Ubuntu-24.04)

```bash
cd "/mnt/c/Users/Vedanti/OneDrive/Desktop/devops project/ansible"

# Ping target
ANSIBLE_CONFIG=ansible.cfg ansible -i inventory/hosts.ini -m ping all

# Full provision (Task 13)
ANSIBLE_CONFIG=ansible.cfg ansible-playbook -i inventory/hosts.ini playbook.yml

# Health check only (Task 14)
ANSIBLE_CONFIG=ansible.cfg ansible-playbook -i inventory/hosts.ini healthcheck.yml

# Teardown (keep data)
ANSIBLE_CONFIG=ansible.cfg ansible-playbook -i inventory/hosts.ini teardown.yml

# Teardown + delete data
ANSIBLE_CONFIG=ansible.cfg ansible-playbook -i inventory/hosts.ini teardown.yml -e "remove_data=true"

# Rollback to a specific tag
ANSIBLE_CONFIG=ansible.cfg ansible-playbook -i inventory/hosts.ini rollback.yml -e "rollback_tag=1.0.5"
```

## Host-port allocation
| Port | Service |
|------|---------|
| 8080 | Tomcat (host) |
| 8081 | Jenkins |
| 8082 | Dev container (Jenkins CD) |
| 8083 | Test container (Jenkins CD) |
| **8085** | **Ansible-managed container** |
