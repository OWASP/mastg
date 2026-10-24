#!/bin/bash
# SUMMARY: This script captures the returned URI and provider metadata from logcat.
adb logcat -d -s RESULT_ATTACK > output.txt
