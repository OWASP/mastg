#!/bin/bash
grep -n -A1 "EnableSafeBrowsing" ./AndroidManifest.xml > output.txt
