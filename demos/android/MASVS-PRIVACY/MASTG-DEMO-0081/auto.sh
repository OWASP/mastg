#!/usr/bin/env bash
set -euo pipefail

FLOW="flow.yaml"

# Start Frida and redirect stdout and stderr to file
./run.sh 2>&1 &

FRIDA_PID=$!

# Run Maestro (https://docs.maestro.dev/getting-started/installing-maestro)
maestro test "$FLOW" > auto.log 2>&1
MAESTRO_EXIT=$?

# Stop Frida when Maestro completes
kill "$FRIDA_PID" 2>/dev/null || true

exit "$MAESTRO_EXIT"
