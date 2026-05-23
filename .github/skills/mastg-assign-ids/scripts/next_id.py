#!/usr/bin/env python3
"""
next_id.py – Print the next available ID for each MASTG component type.

Scans the repository for the highest existing numeric ID of each component
type and prints the next available one. Run from the repository root.

Usage:
    python3 .github/scripts/next_id.py
"""

import re
import glob

# Glob patterns that cover every location where each component type can appear.
# For MASTG-DEMO the pattern matches directory names inside demos/.
COMPONENTS = {
    "MASTG-APP":  ["apps/**/*.md"],
    "MASTG-BEST": ["best-practices/*.md"],
    "MASTG-DEMO": ["demos/**"],
    "MASTG-KNOW": ["knowledge/**/*.md"],
    "MASTG-TECH": ["techniques/**/*.md"],
    "MASTG-TEST": ["tests-beta/**/*.md"],
    "MASTG-TOOL": ["tools/**/*.md"],
}

def main():
    for prefix, patterns in sorted(COMPONENTS.items()):
        ids = []
        for pat in patterns:
            for path in glob.glob(pat, recursive=True):
                m = re.search(rf"{re.escape(prefix)}-(\d{{4}})", path)
                if m:
                    ids.append(int(m.group(1)))
        nxt = max(ids) + 1 if ids else 1
        print(f"{prefix}-{nxt:04d}")

if __name__ == "__main__":
    main()
