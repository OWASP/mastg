#!/bin/bash

# SUMMARY: This script captures the root detection results and the app's reaction from logcat.
# It filters by the app's current process to leave out the entries from previous runs.

pid="$(adb shell pidof org.owasp.mastestapp)"

if [ -z "$pid" ]; then
    echo "The app is not running. Open it and tap Start before running this script." >&2
    exit 1
fi

adb logcat -d -s MASTG --pid "$pid" > output.txt
