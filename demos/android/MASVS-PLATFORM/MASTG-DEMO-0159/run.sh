#!/bin/bash

# Run semgrep to detect accessibility data sensitivity configurations
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-accessibility-data-sensitive.yaml ./MastgTest_reversed.java --text > output.txt
