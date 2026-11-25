#!/bin/bash
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-xposed-detection.yml ./MastgTest_reversed.java --text -o output.txt