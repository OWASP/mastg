#!/usr/bin/env bash
# verify.sh — Check that no fake IDs remain in branch-changed files (excludes .github/).
# Run from the repository root.
# Exits 0 if clean, 1 if fake IDs are still found.

set -euo pipefail

CHANGED=$(git diff --name-only origin/master...HEAD | grep -v "^\.github/")

# Matches both standard fake IDs (MASTG-TYPE-0x##) and non-standard ones
# where the suffix contains lowercase hex chars (e.g. MASTG-BEST-00ea).
FAKE_PATTERN="MASTG-[A-Z]+-0x|MASTG-[A-Z]+-[0-9]*[a-f][0-9a-f]*"

HITS=$(echo "$CHANGED" | xargs grep -lE "$FAKE_PATTERN" 2>/dev/null || true)

if [ -z "$HITS" ]; then
    echo "OK: no fake IDs remaining in branch-changed files."
    exit 0
else
    echo "FAIL: fake IDs still found in:"
    echo "$HITS"
    exit 1
fi
