---
platform: ios
title: Declaring Sensitive Permissions in embedded.mobileprovision
id: MASTG-DEMO-0x69-1
code: [swift]
test: MASTG-TEST-0x69-1
tools: [MASTG-TOOL-0063]
---

## Sample

The snippet below shows sample entitlements declared in the provisioning profile. The embedded.mobileprovision file contains multiple entitlements that the app uses to request special permissions.

{{ ../MASTG-DEMO-0x69/embedded.mobileprovision }}

## Steps

1. Sign the `.ipa` using @MASTG-TECH-0092.
2. Unzip the app package using @MASTG-TECH-0058 and locate the provisioning profile at `./Payload/MASTestApp.app/embedded.mobileprovision`.
3. Run the `run.sh` script to decode the binary formatted profile into a readable XML format.

{{ run.sh }}

## Observation

The output contains the decoded provisioning profile in XML format, including the `Entitlements` dictionary with all capabilities granted to the app.

{{ output.txt }}

## Evaluation

The test fails because the provisioning profile grants excessive entitlements not justified by the app's core functionality. Specifically:

- `com.apple.developer.healthkit` and related keys (`healthkit.access`, `healthkit.background-delivery`, `healthkit.recalibrate-estimates`) — extensive health data access
- `com.apple.developer.homekit` — smart home device control
- `com.apple.developer.siri` — Siri integration
- `com.apple.developer.networking.vpn.api` — VPN configuration
- `com.apple.developer.nfc.readersession.formats` — NFC reader access (NDEF, TAG, PACE)
- `get-task-allow` — debugger attachment (should be disabled in production builds)
