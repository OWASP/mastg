#!/bin/bash
semgrep -c ../rules/mastg-android-data-exposure-via-ipc-query.yml MastgTest_reversed.java > output.txt