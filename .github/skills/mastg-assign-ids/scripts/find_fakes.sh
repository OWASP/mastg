#!/usr/bin/env bash
# find_fakes.sh — List fake-ID occurrences in the current branch (excludes .github/).
# Run from the repository root.
#
# Output:
#   FILE NAMES containing a fake ID (renamed/added in this branch)
#   FILE CONTENTS containing a fake ID (modified in this branch)

set -euo pipefail

CHANGED=$(git diff --name-only origin/master...HEAD | grep -v "^\.github/")

echo "=== Fake IDs in file/dir names ==="
echo "$CHANGED" | grep -E "MASTG-[A-Z]+-0x" || echo "(none)"

echo ""
echo "=== Fake IDs in file contents ==="
echo "$CHANGED" | xargs grep -l "MASTG-[A-Z]*-0x" 2>/dev/null || echo "(none)"
