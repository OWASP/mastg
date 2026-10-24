#!/bin/bash

# Run semgrep to detect the SYSTEM_ALERT_WINDOW permission in the reversed manifest
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-system-alert-window.yml ./AndroidManifest_reversed.xml --text > output.txt
