---
name: mastg-assign-ids
description: Assign real MASTG IDs to draft files that use fake placeholder IDs (e.g. MASTG-KNOW-0x01, MASTG-BEST-0x56). Use when finishing a PR that introduces new MASTG components with placeholder IDs, or when asked to "fix fake IDs", "assign real IDs", or "use next available IDs". Runs next_id.py to get the correct next IDs, renames all affected files, and replaces all in-content references.
---

# MASTG Assign IDs

Replace all fake/placeholder component IDs (e.g. `MASTG-KNOW-0x01`, `MASTG-BEST-0x56`) with the next real sequential IDs across file names and file contents.

## When to use

- A PR adds new MASTG components using `0x##` placeholder IDs
- You are asked to "fix fake IDs", "assign real IDs", or "use next available IDs"

## Fake-ID convention

New draft items use the pattern `MASTG-TYPE-0x##` (hex suffix, e.g. `0x01`, `0x0a`).
Real IDs use zero-padded 4-digit decimal: `MASTG-TYPE-NNNN` (e.g. `MASTG-KNOW-0122`).

## Step-by-step workflow

All scripts are in `scripts/` relative to this skill. Run from the repository root.

### 1. Find all fake-ID files in the current branch

```bash
bash .github/skills/mastg-assign-ids/scripts/find_fakes.sh
```

See [scripts/find_fakes.sh](scripts/find_fakes.sh).

### 2. Get the next available real IDs

```bash
python3 .github/skills/mastg-assign-ids/scripts/next_id.py
```

Prints one line per component type, e.g.:

```txt
MASTG-APP-0034
MASTG-BEST-0046
MASTG-DEMO-0542
MASTG-KNOW-0132
MASTG-TECH-0156
MASTG-TEST-0351
MASTG-TOOL-0151
```

If multiple new components of the same type are introduced in the same PR,
allocate IDs sequentially: first new file → next_id, second → next_id+1, etc.

See [scripts/next_id.py](scripts/next_id.py).

### 3. Build the replacement mapping

Map each fake ID to the real ID you just allocated. Record the mapping explicitly before proceeding, e.g.:

```txt
MASTG-KNOW-0x01 → MASTG-KNOW-0122
MASTG-KNOW-0x02 → MASTG-KNOW-0123
MASTG-BEST-0x56 → MASTG-BEST-0045
```

Important ordering rules: pass longer/more-specific patterns first (e.g. `0x0a` before `0x01`, `0xXX` before anything else).

### 4. Rename files

Use `git mv` to preserve history:

```bash
git mv old-path/MASTG-TYPE-0x01.md new-path/MASTG-TYPE-NNNN.md
```

For demos (directory-based IDs):

```bash
git mv demos/ios/MASVS-CAT/MASTG-DEMO-0x01 demos/ios/MASVS-CAT/MASTG-DEMO-NNNN
```

### 5. Replace in-content references

Pass `OLD=NEW` pairs as arguments — longer patterns first:

```bash
python3 .github/skills/mastg-assign-ids/scripts/fix_ids.py \
    MASTG-KNOW-0x0a=MASTG-KNOW-0131 \
    MASTG-KNOW-0x01=MASTG-KNOW-0122 \
    MASTG-BEST-0x56=MASTG-BEST-0045
```

This covers frontmatter `id:` fields and `knowledge:` lists automatically (they live in `.md` files). See [scripts/fix_ids.py](scripts/fix_ids.py).

### 6. Verify

```bash
bash .github/skills/mastg-assign-ids/scripts/verify.sh
python3 .github/skills/mastg-assign-ids/scripts/next_id.py
```

`verify.sh` exits 1 if any fake IDs remain. `next_id.py` should now report incremented next IDs. See [scripts/verify.sh](scripts/verify.sh).

### 7. Give the user a summary of the changes

Provide a summary of the ID changes, e.g.:

```md
ID assignments used in this PR:

| Fake ID | Real ID |
|---------|---------|
| MASTG-KNOW-0x01 | MASTG-KNOW-0122 |
| MASTG-KNOW-0x02 | MASTG-KNOW-0123 |
| MASTG-BEST-0x56 | MASTG-BEST-0045 |
```

## Component locations

| Type | Files/dirs |
|------|-----------|
| MASTG-APP | `apps/*.md` |
| MASTG-BEST | `best-practices/*.md` |
| MASTG-DEMO | `demos/<platform>/<MASVS-CAT>/MASTG-DEMO-NNNN/` (directory) |
| MASTG-KNOW | `knowledge/<platform>/<MASVS-CAT>/MASTG-KNOW-NNNN.md` |
| MASTG-TECH | `techniques/<platform>/MASTG-TECH-NNNN.md` |
| MASTG-TEST | `tests-beta/<platform>/<MASVS-CAT>/MASTG-TEST-NNNN.md` |
| MASTG-TOOL | `tools/<type>/MASTG-TOOL-NNNN.md` |

## Important notes

- Always use `git mv`, never `mv`, so renames are tracked in history.
- Always scope work to files changed in the current branch. Never touch the whole repo.
- Exclude `.github/` from all searches and replacements (all scripts do this automatically).
- The `id:` field in frontmatter must match the filename.
