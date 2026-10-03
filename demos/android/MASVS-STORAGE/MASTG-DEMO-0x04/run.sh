#!/bin/bash

NO_COLOR=true semgrep --disable-version-check -c ../../../../rules/mastg-android-hardcoded-api-keys.yml ./MastgTest_reversed.java ./strings.xml --text > output.txt 2>&1
