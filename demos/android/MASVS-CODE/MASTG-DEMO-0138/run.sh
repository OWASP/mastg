#!/bin/bash
# SUMMARY: This script uses semgrep to detect implicit intents that leak sensitive data via extras.
NO_COLOR=true semgrep --config rule.yaml ../MASTG-DEMO-0136/MastgTest_reversed.java --text > output.txt
