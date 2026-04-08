#!/bin/bash
plistutil -i Info.plist -f xml | grep -i -A 1 UsageDescription > output.txt