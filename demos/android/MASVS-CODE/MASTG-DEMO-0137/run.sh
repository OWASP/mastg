#!/bin/bash
# SUMMARY: This script uses semgrep to find all exported activities in the manifest.
NO_COLOR=true semgrep --config rule.yaml ../MASTG-DEMO-0136/AndroidManifest_reversed.xml --text > output.txt
