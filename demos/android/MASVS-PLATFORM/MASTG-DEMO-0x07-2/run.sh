#!/bin/bash
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-Data-Exposure-via-IPC-secured.yml ./AndroidManifest_reversed.xml -o output.txt