#!/usr/bin/env bash
set -euo pipefail

ALIAS_CMD="alias runMasWebsite=\"${WORKSPACE_FOLDER}/.devcontainer/runMasWebsite.sh\""
MESSAGE_CMD='echo "In case the development server is not running, run '"'"'runMasWebsite'"'"'."'

for rcfile in "$HOME/.bashrc" "$HOME/.zshrc"; do
    touch "$rcfile"
    grep -qxF "$ALIAS_CMD" "$rcfile" || echo "$ALIAS_CMD" >> "$rcfile"
    grep -qxF "$MESSAGE_CMD" "$rcfile" || echo "$MESSAGE_CMD" >> "$rcfile"
done
