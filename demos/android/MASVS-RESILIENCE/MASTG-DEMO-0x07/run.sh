#!/bin/bash

NO_COLOR=true semgrep -c ../../../../rules/mastg-android-device-attestation-apis.yml ./MastgTest_reversed.java > output.txt
