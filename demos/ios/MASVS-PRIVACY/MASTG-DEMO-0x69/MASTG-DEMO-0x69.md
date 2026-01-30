---
platform: ios
title: Declaring Sensitive Permissions in Info.plist
code: [swift]
id: MASTG-DEMO-0x69
test: MASTG-TEST-0x69
---

## Sample

The code snippet below shows sample code that accesses protected resources requiring purpose strings. The `Info.plist` file declares multiple usage descriptions that the app uses to request permissions.

{{ MastgTest.swift }}

{{ Info.plist }}

## Steps

1. Extract the app package content using (@MASTG-TOOL-0126) and locate the `Info.plist` file, which in located at `./Payload/MASTestApp.app/Info.plist`.
2. Run the `run.sh` script to parse the Info.plist and find all occurrences of permission related keys (those ending in `UsageDescription`).

{{ run.sh }}

## Observation

The output lists 16 purpose strings found in the `Info.plist` file, indicating the app declares access to a wide range of protected resources.

{{ output.txt }}

## Evaluation

The test fails because the app declares excessive purpose strings not justified by its core functionality. Specifically:

- `NSLocationAlwaysAndWhenInUseUsageDescription` and `NSLocationWhenInUseUsageDescription` — continuous and foreground location access
- `NSHealthShareUsageDescription` — health data read access
- `NSHomeKitUsageDescription` — smart home device control
- `NSCameraUsageDescription`, `NSMicrophoneUsageDescription` — camera and microphone access
- `NSContactsUsageDescription`, `NSCalendarsUsageDescription` — contacts and calendar access
- `NFCReaderUsageDescription` — NFC reader access
- `NSSiriUsageDescription` — Siri integration
