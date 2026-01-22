#!/bin/bash

# Frooky wrapper for iOS demos
# Usage: run-ios.sh <hooks_file> [output_file]
# Runs frooky from utils/frooky/ to keep node_modules centralized

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
DEMO_DIR="$(pwd)"

HOOKS_FILE="${1:-hooks.json}"
OUTPUT_FILE="${2:-output.json}"

# Convert to absolute paths
HOOKS_PATH="$DEMO_DIR/$HOOKS_FILE"
OUTPUT_PATH="$DEMO_DIR/$OUTPUT_FILE"

# Run frooky from the utils/frooky directory
cd "$SCRIPT_DIR"
frooky -U -f org.owasp.mastestapp.MASTestApp-iOS --platform ios -o "$OUTPUT_PATH" "$HOOKS_PATH"
