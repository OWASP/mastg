#!/bin/bash

# Clear previous logs, then capture the attacker and victim messages
adb logcat -c
adb logcat -s MASTG-A11Y:I MASTG-DEMO:I > output.txt
