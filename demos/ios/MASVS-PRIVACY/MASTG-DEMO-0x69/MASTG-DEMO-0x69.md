---
platform: ios
title: Declaring Sensitive Permissions in Info.plist
id: MASTG-DEMO-0x69
code: [swift]
test: MASTG-TEST-0x69
tools: [MASTG-TOOL-0126]
---

## Sample

The snippet below shows sample code that accesses protected resources requiring purpose strings. The app bundle contains an `Info.plist` file, and the rendered sample uses `Info_reversed.plist` so you can review the declared usage descriptions in XML form.

{{ MastgTest.swift # Info_reversed.plist }}

## Steps

1. Extract the app package content using @MASTG-TOOL-0126 and locate the `Info.plist` file, which is located at `./Payload/MASTestApp.app/Info.plist`.
2. Run the `run.sh` script to parse the Info.plist and find all occurrences of permission related keys (those ending in `UsageDescription`) along with their corresponding purpose strings.

{{ run.sh }}

## Observation

The output contains all `UsageDescription` keys found in the `Info.plist` file, each paired with its corresponding description string.

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
