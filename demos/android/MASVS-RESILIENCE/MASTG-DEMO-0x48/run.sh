#!/bin/bash
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-frida-detection.yml ./MastgTest_reversed.java --text -o output.txt