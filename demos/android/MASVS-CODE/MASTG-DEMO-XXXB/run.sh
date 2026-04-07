#!/bin/bash
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-custom-intent-filter-intercept.yml AndroidManifest_reversed.xml --text -o output.txt