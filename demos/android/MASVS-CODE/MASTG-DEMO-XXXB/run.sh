#!/bin/bash
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-custom-intent-filter-intercept.yml AndroidManifest.xml --text -o output.txt