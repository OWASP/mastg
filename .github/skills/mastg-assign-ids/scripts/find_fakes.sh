#!/usr/bin/env bash
# find_fakes.sh — List fake-ID occurrences in the current branch (excludes .github/).
# Run from the repository root.
#
# Output:
#   FILE NAMES containing a fake ID (renamed/added in this branch)
#   FILE CONTENTS containing a fake ID (modified in this branch)

set -euo pipefail

CHANGED=$(git diff --name-only origin/master...HEAD | grep -v "^\.github/")

# Matches both standard fake IDs (MASTG-TYPE-0x##) and non-standard ones
# where the suffix contains lowercase hex chars (e.g. MASTG-BEST-00ea).
FAKE_PATTERN="MASTG-[A-Z]+-0x|MASTG-[A-Z]+-[0-9]*[a-f][0-9a-f]*"

echo "=== Fake IDs in file/dir names ==="
echo "$CHANGED" | grep -E "$FAKE_PATTERN" || echo "(none)"

echo ""
echo "=== Fake IDs in file contents ==="
echo "$CHANGED" | xargs grep -lE "$FAKE_PATTERN" 2>/dev/null || echo "(none)"
