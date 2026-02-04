#!/bin/bash

# Run semgrep to detect root detection code
semgrep --config ../../../../rules/mastg-android-root-detection.yaml ./MastgTest_reversed.java --text > output.txt
