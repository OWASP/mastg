#!/bin/bash
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-key-attestation-missing-challenge.yml ./MastgTest_reversed.java > output.txt
