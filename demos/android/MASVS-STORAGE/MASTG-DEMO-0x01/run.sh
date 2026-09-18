#!/bin/bash
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-unencrypted-internal-file-storage.yml ./MastgTest_reversed.java > output.txt
