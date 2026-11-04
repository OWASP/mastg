#!/bin/bash

NO_COLOR=true semgrep --disable-version-check -c ../../../../rules/mastg-android-device-attestation-apis.yml ./MastgTest_reversed.java --text > output.txt 2>&1
