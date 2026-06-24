#!/bin/bash
# SUMMARY: This script uses semgrep to detect implicit intents in the source code.
NO_COLOR=true semgrep --config ../../../../rules/mastg-android-implicit-intent-internal-communication.yml MastgTest_reversed.java --text > output.txt
