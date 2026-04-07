#!/bin/bash
plistutil -i ./Payload/MASTestApp.app/Info.plist -f xml | grep -i -A 1 UsageDescription > output.txt