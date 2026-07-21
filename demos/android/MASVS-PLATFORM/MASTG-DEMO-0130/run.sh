#!/bin/bash

# Stage 1 (capture) — enumerate the manifest-declared receivers. The rule flags every
# receiver that is exported without an android:permission, and lists the permission-protected
# ones separately so they can be triaged.
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-exported-receiver.yml ./AndroidManifest_reversed.xml --text --max-lines-per-finding 0 > manifest_scan.txt
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-exported-receiver.yml ./AndroidManifest_reversed.xml --json > manifest_scan.json 2>/dev/null

# Stage 2 (inspect code) — locate the onReceive entry point and the attacker-controllable
# intent extras it reads in the decompiled code.
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-receiver-entrypoints.yml ./MastgTest_reversed.java --text --max-lines-per-finding 0 > code_scan.txt
