#!/bin/bash
# SUMMARY: This script captures the intercepted implicit intent details from logcat.
adb logcat -d -s INTENT_ATTACK > output.txt
