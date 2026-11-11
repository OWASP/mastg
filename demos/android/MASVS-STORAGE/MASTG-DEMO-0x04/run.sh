#!/bin/bash

NO_COLOR=true semgrep -c ../../../../rules/mastg-android-hardcoded-api-keys.yml ./MastgTest_reversed.java ./strings.xml > output.txt
