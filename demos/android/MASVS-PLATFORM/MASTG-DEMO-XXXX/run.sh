#!/bin/bash
semgrep -c ../rules/mastg-android-data-exposure-via-ipc-read.yml MastgTest_reversed.java > output.txt