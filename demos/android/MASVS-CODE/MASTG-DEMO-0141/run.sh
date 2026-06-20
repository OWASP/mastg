#!/bin/bash
# SUMMARY: This script uses semgrep to detect the exported ContentProvider in the attacker app manifest.
NO_COLOR=true semgrep --config rule.yaml AndroidManifest.xml --text > output.txt
