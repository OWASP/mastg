#!/bin/bash
NO_COLOR=true semgrep \
  -c ../../../../rules/mastg-android-local-storage-integrity-validation.yaml \
  --text -o output.txt \
  ./MastgTest_reversed.java
