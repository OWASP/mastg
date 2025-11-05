#!/bin/bash

# MASTG-DEMO-0091: Testing Frida Detection
# This script demonstrates Frida detection and bypass techniques

BUNDLE_ID="com.example.MASTestApp"  # Update with your actual bundle ID

echo "=========================================="
echo "MASTG-DEMO-0091: Testing Frida Detection"
echo "=========================================="
echo ""
echo "Prerequisites:"
echo "  - iOS Simulator running"
echo "  - Frida installed: pip3 install frida-tools"
echo "  - App installed on simulator"
echo ""

# Test 1: Clean run
echo "[Test 1] Running WITHOUT Frida"
echo "--------------------------------------"
echo "Launch the app manually and press 'Start'"
echo "Expected: ✅ No Frida detected"
echo ""
read -p "Press Enter to continue to Test 2..."

# Test 2: With Frida
echo ""
echo "[Test 2] Running WITH Frida"
echo "--------------------------------------"
echo "Attaching Frida to the app..."

# Find the app process
APP_PID=$(ps aux | grep MASTestApp | grep -v grep | awk '{print $2}' | head -1)

if [ -z "$APP_PID" ]; then
    echo "Error: MASTestApp is not running!"
    echo "Please launch the app first."
    exit 1
fi

echo "Found MASTestApp with PID: $APP_PID"
echo "Attaching Frida..."
echo ""
echo "In another terminal, run:"
echo "  frida -p $APP_PID"
echo ""
echo "Then press 'Start' in the app."
echo "Expected: 🚨 SECURITY ALERT with detection results"
echo ""
read -p "Press Enter to continue to Test 3..."

# Test 3: With bypass
echo ""
echo "[Test 3] Running WITH Frida + Bypass"
echo "--------------------------------------"
echo "Killing app and spawning with bypass script..."

pkill -9 MASTestApp 2>/dev/null
sleep 1

if [ ! -f "frida-bypass.js" ]; then
    echo "Error: frida-bypass.js not found!"
    echo "Please ensure frida-bypass.js is in the same directory."
    exit 1
fi

echo "Spawning app with bypass..."
frida -f "$BUNDLE_ID" -l frida-bypass.js --no-pause

echo ""
echo "=========================================="
echo "Demo Complete!"
echo "=========================================="
echo ""
echo "Summary:"
echo "  Test 1: No detection (clean run)"
echo "  Test 2: Detections triggered"
echo "  Test 3: Detections bypassed"
