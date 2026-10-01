#!/bin/bash
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-device-security-state.yml ./MastgTest_reversed.java --text > output.txt
