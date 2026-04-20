#!/bin/bash

# Run semgrep to detect overlay protection mechanisms
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-overlay-protection.yml ./MastgTest_reversed.java --text > output.txt
