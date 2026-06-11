#!/bin/bash
set -euo pipefail

# Extract the entitlements from the app's main binary (see @MASTG-TECH-0111).
rabin2 -OC "../MASTG-DEMO-0126/MASTestApp" | sed -n '1,/<\/plist>/p' > "entitlements_reversed.plist"
