---
platform: ios
title: Extracting Entitlements from the App Binary's Code Signature
code: [swift]
id: MASTG-DEMO-0x69-2
test: MASTG-TEST-0x69-2
tools: [MASTG-TOOL-0073]
---

## Sample

The code snippet below shows sample code that accesses protected resources requiring entitlements. The app's entitlements are embedded in the binary's code signature and declare multiple permissions that the app uses to request special access.

{{ ../MASTG-DEMO-0x69/MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main app binary (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Run `run.sh` to extract the entitlements.

{{ run.sh }}

## Observation

The output should contain the entitlements plist extracted from the app binary's code signature, listing all capabilities embedded during the signing process.

{{ output.txt }}

## Evaluation

The test fails because the app binary embeds entitlements that are excessive for its core functionality. Specifically:

- `com.apple.developer.healthkit` — health data access
- `com.apple.developer.homekit` — smart home device control
- `com.apple.developer.siri` — Siri integration
- `com.apple.developer.nfc.readersession.formats` — NFC reader access (NDEF, TAG)
- `com.apple.developer.networking.wifi-info` — Wi-Fi network information
- `get-task-allow` — debugger attachment (should be disabled in production builds)
