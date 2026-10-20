# Git Branching Policy

**Project:** Telecom Tower Maintenance Portal  
**Repo:** https://github.com/Vedanti1703/devops  

---

## 1. Branch Structure

| Branch Pattern | Purpose | Who Uses It | Merge Target |
|---|---|---|---|
| `main` | Stable, production-grade releases only. Tagged with semantic version (`v1.0.0`). | Release Manager / Admin | — |
| `develop` | Continuous integration branch. All features are merged here first. | Developers | `main` (via PR) |
| `feature/<short-name>` | Individual feature development. Branched off `develop`. | Any Developer | `develop` (via PR) |
| `bugfix/<short-name>` | Targeted bug fix branched from `develop`. | Developer / Tester | `develop` (via PR) |
| `hotfix/<short-name>` | Critical production fix branched directly from `main`. | Senior Developer | `main` + `develop` |

---

## 2. Workflow Rules

1. **No Direct Pushes to `main` or `develop`.**  
   All changes must go through a Pull Request (PR) with at least one review approval.

2. **Branch from the Correct Base.**  
   - New features: `git checkout -b feature/<name> develop`
   - Bug fixes: `git checkout -b bugfix/<name> develop`
   - Hotfixes: `git checkout -b hotfix/<name> main`

3. **One PR Per Feature/Fix.**  
   Keep PRs small and focused. Large PRs are harder to review and introduce more risk.

4. **Merge Strategy:** Use `--merge` (standard merge commit) to preserve history and traceability.

5. **Delete branches after merge** to keep the repository clean.

---

## 3. Commit Message Convention

Format:
```
<type>: <short imperative description (max 72 chars)>

[Optional body: explain WHY, not WHAT — for non-trivial changes]

[Optional footer: Closes #<issue-number>]
```

### Allowed `<type>` Values

| Type | When to Use |
|---|---|
| `feat` | A new feature or capability |
| `fix` | A bug fix |
| `docs` | Documentation updates only |
| `chore` | Build process, dependency, or tool changes |
| `test` | Adding or fixing automated tests |
| `refactor` | Code improvement without feature change or bug fix |
| `style` | Formatting, whitespace, or naming (no logic change) |

### Examples
```bash
feat: add maintenance record creation form
fix: prevent invalid OPEN->VERIFIED state transition
docs: add agile plan and DevOps lifecycle documentation
chore: add gitignore and maven wrapper
test: add unit tests for status transition workflow
refactor: extract workflow logic into dedicated service
```

---

## 4. Pull Request Checklist

Before opening a PR, confirm all items below:
- [ ] Branch is up to date with `develop` (rebase or merge latest).
- [ ] `mvn clean test` passes locally — zero test failures.
- [ ] All HTML form fields and action buttons have stable `id` attributes.
- [ ] PR title follows commit message format: `type: short description`.
- [ ] PR description references the related GitHub Issue number (`Closes #<n>`).
- [ ] At least one reviewer is assigned.
- [ ] No secrets, credentials, or `.env` files are committed.

---

## 5. Release Tagging Convention

Tags are created on `main` after a verified merge:

```bash
git tag -a v1.0.0 -m "MVP release baseline"
git push origin v1.0.0
```

Format: `v<MAJOR>.<MINOR>.<PATCH>`
- `MAJOR`: Incompatible API or architectural change.
- `MINOR`: New backward-compatible feature.
- `PATCH`: Backward-compatible bug fix.
