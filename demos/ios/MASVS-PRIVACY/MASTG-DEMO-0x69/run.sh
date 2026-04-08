#!/bin/bash
plistutil -i Info_reversed.plist -f xml | grep -i -A 1 UsageDescription > output.txt