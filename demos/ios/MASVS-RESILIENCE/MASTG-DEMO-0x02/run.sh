#!/bin/sh
set -eu

APP_BUNDLE_ID="org.owasp.mastestapp.MASTestApp-iOS"
APP_NAME="MASTestApp"

xcrun simctl spawn booted log stream \
  --style compact \
  --level debug \
  --predicate "process CONTAINS[c] \"$APP_NAME\"" \
  > system_log.txt 2>&1 &
LOG_PID=$!

xcrun simctl launch --console-pty booted "$APP_BUNDLE_ID" > output.txt 2>&1 || true

kill "$LOG_PID" 2>/dev/null || true
wait "$LOG_PID" 2>/dev/null || true