#!/bin/bash

PASTEBOARD_PATTERN='UIPasteboard\.general\.string|UIPasteboard\.general\.setItems\('
SHARED_CONTAINER_PATTERN='containerURL\(forSecurityApplicationGroupIdentifier'
FILE_COORDINATION_PATTERN='NSFileCoordinator|coordinate\(writingItemAt'

if [ ! -f MastgTest_reversed.swift ]; then
    echo "MastgTest_reversed.swift not found" >&2
    exit 1
fi

grep -nE "$PASTEBOARD_PATTERN|$SHARED_CONTAINER_PATTERN|$FILE_COORDINATION_PATTERN" MastgTest_reversed.swift > output.txt
grep_status=$?

if [ "$grep_status" -eq 1 ]; then
    : > output.txt
elif [ "$grep_status" -ne 0 ]; then
    echo "grep failed with status $grep_status" >&2
    exit "$grep_status"
fi

# Normalize file ending to avoid trailing blank-line diffs in committed output artifacts.
python - <<'PY'
from pathlib import Path
p = Path("output.txt")
p.write_text(p.read_text().rstrip("\n"))
PY
