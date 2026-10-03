#!/bin/bash

# Stage 1 (capture) — enumerate the manifest-declared services. The rule flags every service
# that is exported without an android:permission, and lists the permission-protected ones
# separately so they can be triaged.
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-exported-service.yml ./AndroidManifest_reversed.xml --text --max-lines-per-finding 0 > manifest_scan.txt
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-exported-service.yml ./AndroidManifest_reversed.xml --json > manifest_scan.json 2>/dev/null

# Stage 2 (inspect code) — locate the entry points reachable when the service is started or
# bound (onStartCommand, onBind, onRebind, onHandleIntent), plus any runtime caller-permission
# checks, in the decompiled code.
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-service-entrypoints.yml ./MastgTest_reversed.java --text --max-lines-per-finding 0 > code_scan.txt
