#!/bin/bash
# Print the purpose strings (keys and their user-facing explanations) from Info.plist.
# plutil -p renders the plist in a readable form (see @MASTG-TECH-0138).
plutil -p Info.plist | grep -i "UsageDescription" > output_purpose_strings.txt
