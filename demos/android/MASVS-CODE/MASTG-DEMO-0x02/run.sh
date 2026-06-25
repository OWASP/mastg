#!/bin/bash
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-webview-url-handlers.yml ./MastgTest_reversed.java --text -o output.txt 2>/dev/null
