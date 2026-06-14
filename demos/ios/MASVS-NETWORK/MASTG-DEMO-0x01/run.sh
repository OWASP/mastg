#!/bin/bash

# Extract the NSAppTransportSecurity configuration from Info.plist
# Using grep and sed to extract the relevant section
grep -A 10 "NSAppTransportSecurity" Info.plist > output.txt

# Show the result
cat output.txt
