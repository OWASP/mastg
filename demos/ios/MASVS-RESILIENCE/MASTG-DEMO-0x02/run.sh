#!/bin/sh

APP="${APP:-MASTestApp}"
SCRIPT="${SCRIPT:-script.js}"

trap 'trap - INT TERM EXIT, kill 0 2>/dev/null' INT TERM EXIT

idevicesyslog -p "$APP" \
  | awk -v app="$APP" '$0 !~ app "\\((UIKitCore|CoreFoundation|IOKit|FrontBoardServices)\\)" { print; fflush() }' \
  > unified.log 2>&1 &

stdbuf -oL -eL frida -U -n "$APP" -l "$SCRIPT" \
  > frida.log 2>&1 &

wait