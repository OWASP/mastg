#!/bin/bash
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-webview-safebrowsing.yml ./AndroidManifest_reversed.xml ./MastgTestWebView_reversed.java --text -o output.txt
