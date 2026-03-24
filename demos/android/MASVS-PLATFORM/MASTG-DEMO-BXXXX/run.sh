#!/bin/bash
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-Data-Exposure-via-IPC-secured.yml ./MastgTest_reversed.java -o output.txt