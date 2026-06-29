#!/bin/bash
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-webview-url-handlers.yml ./MastgTestWebView_reversed.java --text > output.txt
