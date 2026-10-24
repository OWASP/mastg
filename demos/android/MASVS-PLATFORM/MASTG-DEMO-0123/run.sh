#!/bin/bash
# Dump the log buffer filtered to the EXFIL tag.
adb logcat -d -s EXFIL > output.txt
