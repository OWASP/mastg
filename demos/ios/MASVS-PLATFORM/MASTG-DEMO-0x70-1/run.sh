#!/bin/bash
plistutil -i entitlements.plist -f xml | grep -A 3 "com.apple.developer.associated-domains" > output.txt