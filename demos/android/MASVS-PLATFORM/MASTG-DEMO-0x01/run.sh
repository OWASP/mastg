#!/bin/bash

# Run semgrep rules on the layout XML file
echo "=== Scanning XML Layout ==="
semgrep --config ../../../../rules/mastg-android-accessibility-data-sensitive.yaml activity_login.xml --text > output.txt 2>&1

# Run semgrep rules on the Kotlin source code
echo "" >> output.txt
echo "=== Scanning Kotlin Code ===" >> output.txt
semgrep --config ../../../../rules/mastg-android-accessibility-data-sensitive.yaml MastgTest.kt --text >> output.txt 2>&1

cat output.txt
