#!/bin/bash
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-xposed-detection.yaml ./MastgTest_reversed.java > output.txt