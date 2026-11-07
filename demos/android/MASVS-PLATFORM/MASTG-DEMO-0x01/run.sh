#!/bin/bash

# Run semgrep to detect accessibility data sensitivity configurations, including Jetpack Compose semantics
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-accessibility-data-sensitive.yaml MainActivityKt_reversed.java ComposableSingletons\$MainActivityKt_reversed.java --text > output.txt
