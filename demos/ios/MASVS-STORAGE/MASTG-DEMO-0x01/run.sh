#!/bin/bash

# Static analysis of App Group / Keychain data sharing across the app and its Share Extension.
# Binaries are extracted from the built IPA:
#   - MASTestApp        : Payload/MASTestApp.app/MASTestApp
#   - ShareExtension    : Payload/MASTestApp.app/PlugIns/ShareExtension.appex/ShareExtension

echo "=== App entitlements (App Group + Keychain Access Group) ==="
rabin2 -OC MASTestApp | grep -A1 "com.apple.security.application-groups\|keychain-access-groups"

echo ""
echo "=== Extension entitlements (App Group + Keychain Access Group) ==="
rabin2 -OC ShareExtension | grep -A1 "com.apple.security.application-groups\|keychain-access-groups"

echo ""
echo "=== Main app: stores the token in the shared Keychain (PASS) ==="
r2 -q -i app_keychain.r2 -A MASTestApp

echo ""
echo "=== Share Extension: caches the token unencrypted in the shared container (FAIL) ==="
r2 -q -i extension_shared_storage.r2 -A ShareExtension
