#!/bin/bash

# Run semgrep to detect setHideOverlayWindows in the decompiled code and HIDE_OVERLAY_WINDOWS permission in the manifest
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-overlay-protection.yml ./MastgTest_reversed.java ./AndroidManifest_reversed.xml --text > output.txt
