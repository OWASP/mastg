#!/usr/bin/env python3
"""
next_id.py – Print the next available ID for each MASTG component type.

Scans the repository for the highest existing numeric ID of each component
type and prints the next available one. Run from the repository root.

Usage:
    python3 .github/scripts/next_id.py
"""

import re
import subprocess

# Component type prefixes to scan for.
COMPONENTS = [
    "MASTG-APP",
    "MASTG-BEST",
    "MASTG-DEMO",
    "MASTG-KNOW",
    "MASTG-TECH",
    "MASTG-TEST",
    "MASTG-TOOL",
]


def get_tracked_paths():
    """Return all paths tracked by git (staged + committed). Excludes untracked
    files and empty directories, which could inflate the highest ID."""
    result = subprocess.run(
        ["git", "ls-files"],
        capture_output=True, text=True, check=True,
    )
    return result.stdout.splitlines()


def main():
    paths = get_tracked_paths()
    for prefix in sorted(COMPONENTS):
        ids = []
        pattern = re.compile(rf"{re.escape(prefix)}-(\d{{4}})")
        for path in paths:
            m = pattern.search(path)
            if m:
                ids.append(int(m.group(1)))
        nxt = max(ids) + 1 if ids else 1
        print(f"{prefix}-{nxt:04d}")


if __name__ == "__main__":
    main()
