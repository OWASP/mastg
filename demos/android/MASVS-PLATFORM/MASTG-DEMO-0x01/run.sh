#!/bin/bash

# Context-registered receivers are registered at runtime with Context.registerReceiver and do
# NOT appear in the AndroidManifest, so manifest enumeration alone misses them.

# Stage 1 (capture, manifest) — enumerate the manifest-declared receivers. This confirms the app
# declares no receiver of its own in the manifest (only the library ProfileInstallReceiver shows up).
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-exported-receiver.yml ./AndroidManifest_reversed.xml --text --max-lines-per-finding 0 > manifest_scan.txt

# Stage 1b (capture, permissions) — list the custom permissions declared in the manifest with
# their protection levels, and flag weak ones (normal/dangerous, or no level) that untrusted apps
# can hold. evaluate.sh uses this to resolve each receiver's broadcastPermission to its level.
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-declared-permission-protection-level.yml ./AndroidManifest_reversed.xml --text --max-lines-per-finding 0 > permissions_scan.txt
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-declared-permission-protection-level.yml ./AndroidManifest_reversed.xml --json > permissions_scan.json 2>/dev/null

# Stage 2 (capture, code) — search the decompiled code for context-registered receivers and
# classify each registration as exported (RECEIVER_EXPORTED, flag 2), not exported
# (RECEIVER_NOT_EXPORTED, flag 4), or registered without an explicit flag.
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-context-registered-receiver.yml ./MastgTest_reversed.java --text --max-lines-per-finding 0 > code_scan.txt
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-context-registered-receiver.yml ./MastgTest_reversed.java --json > code_scan.json 2>/dev/null

# Stage 3 (inspect code) — locate the onReceive entry points and the attacker-controllable intent
# extras they read, to review for sensitive functionality.
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-receiver-entrypoints.yml ./MastgTest_reversed.java --text --max-lines-per-finding 0 > onreceive_scan.txt
