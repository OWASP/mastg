#!/bin/bash
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-local-storage-input-validation.yml ./MastgTest_reversed.java > output.txt