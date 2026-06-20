#!/bin/bash

# This script demonstrates static analysis of app extensions and App Groups usage

echo "=== Checking for App Extensions ==="
echo ""
echo "Looking for NSExtensionPointIdentifier in Info.plist files..."
grep -n "NSExtensionPointIdentifier" ShareExtension_Info.plist

echo ""
echo "=== Extension Type and Activation Rules ==="
echo ""
echo "Extension type:"
grep -A 1 "NSExtensionPointIdentifier" ShareExtension_Info.plist | tail -1

echo ""
echo "Supported data types:"
grep -A 3 "NSExtensionActivationRule" ShareExtension_Info.plist | tail -3

echo ""
echo "=== Checking for App Groups Entitlement ==="
echo ""
grep -A 3 "com.apple.security.application-groups" entitlements.plist

echo ""
echo "=== Checking Code for Shared Storage API Usage ==="
echo ""
echo "UserDefaults with suiteName (shared UserDefaults):"
grep -n "UserDefaults(suiteName:" MastgTest.swift

echo ""
echo "FileManager containerURL (shared container access):"
grep -n "containerURL(forSecurityApplicationGroupIdentifier:" MastgTest.swift

echo ""
echo "=== Identifying Sensitive Data in Shared Storage ==="
echo ""
echo "Sensitive data stored in shared UserDefaults:"
grep -B 1 -A 0 'set.*forKey' MastgTest.swift | grep -E "(Email|Token|subscription|apiKey|refreshToken)"

echo ""
echo "Files written to shared container:"
grep -B 2 'write(to: fileURL' MastgTest.swift | grep 'let credentials'
