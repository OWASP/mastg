#!/bin/bash
# Extract the entitlements embedded in the signed binary (see @MASTG-TECH-0111).
rabin2 -OC MASTestApp | sed -n '1,/<\/plist>/p' > entitlements_reversed.plist

# Locate the universal link handler and check for input validation.
r2 -q -i input_validation.r2 -A MASTestApp > output.txt
