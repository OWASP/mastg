#!/bin/bash

# This script demonstrates bypassing root detection using Frida
# Replace <package_name> with the actual package name of the app being tested

PACKAGE_NAME="org.owasp.mastestapp"

echo "Starting Frida to bypass root detection..."
echo "Note: Make sure the app is installed and frida-server is running on the device"
echo ""

# Run Frida with the bypass script
# -U: Connect to USB device
# -f: Spawn the app
# -l: Load the script
# --no-pause: Don't pause after spawning
frida -U -f $PACKAGE_NAME -l frida_script.js --no-pause > output.txt 2>&1 &

FRIDA_PID=$!

# Wait for app to initialize and root checks to be performed
sleep 5

# Stop Frida
kill $FRIDA_PID 2>/dev/null

echo "Output saved to output.txt"
echo ""
echo "Expected output should show detected root checks and bypass confirmations."
