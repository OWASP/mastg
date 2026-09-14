#!/usr/bin/env bash

set -euo pipefail

if [ "${1:-}" = "clear" ]; then
    adb logcat -c
    exit 0
fi

adb logcat -d -s MASTG-A11Y:I '*:S' | tee output.txt
