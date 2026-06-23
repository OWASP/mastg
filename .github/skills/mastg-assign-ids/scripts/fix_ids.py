#!/usr/bin/env python3
"""
fix_ids.py — Replace fake MASTG IDs with real IDs in branch-changed files.

Operates only on files changed vs. origin/master in the current branch.
Excludes .github/.

Usage (run from repository root):
    python3 .github/skills/mastg-assign-ids/scripts/fix_ids.py OLD=NEW [OLD=NEW ...]

Example:
    python3 .github/skills/mastg-assign-ids/scripts/fix_ids.py \\
        MASTG-KNOW-0x0a=MASTG-KNOW-0131 \\
        MASTG-KNOW-0x01=MASTG-KNOW-0122 \\
        MASTG-BEST-0x56=MASTG-BEST-0045

Ordering: pass longer/more-specific patterns first (e.g. 0x0a before 0x01).
"""

import subprocess
import os
import sys


def main():
    if len(sys.argv) < 2:
        print(__doc__)
        sys.exit(1)

    replacements = []
    for arg in sys.argv[1:]:
        if "=" not in arg:
            print(f"Error: expected OLD=NEW, got: {arg}", file=sys.stderr)
            sys.exit(1)
        old, new = arg.split("=", 1)
        replacements.append((old, new))

    committed = subprocess.run(
        ["git", "diff", "--name-only", "--diff-filter=d", "origin/master...HEAD"],
        capture_output=True, text=True, check=True,
    ).stdout.splitlines()
    staged = subprocess.run(
        ["git", "diff", "--cached", "--name-only", "--diff-filter=d"],
        capture_output=True, text=True, check=True,
    ).stdout.splitlines()
    files = sorted({
        p for p in committed + staged
        if not p.startswith(".github/") and os.path.isfile(p)
    })

    for path in files:
        try:
            with open(path, encoding="utf-8") as f:
                text = f.read()
        except (UnicodeDecodeError, IsADirectoryError):
            continue
        updated = text
        for old, new_id in replacements:
            updated = updated.replace(old, new_id)
        if updated != text:
            with open(path, "w") as f:
                f.write(updated)
            print("Updated:", path)


if __name__ == "__main__":
    main()
