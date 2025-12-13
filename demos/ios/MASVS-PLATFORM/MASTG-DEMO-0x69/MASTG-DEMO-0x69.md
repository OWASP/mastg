---
platform: ios
title: Declaring Dangerous Permissions in Info.plist
id: MASTG-DEMO-00x
code: [swift]
test: MASTG-TEST-0069
---

### Sample

The code snippet below shows sample code that accesses protected resources requiring purpose strings. The `Info.plist` file declares multiple usage descriptions that the app uses to request permissions.

{{ Info.plist }}

{{ MastgTest.swift }}


### Steps

1. Extract the app package content using (@MASTG-TOOL-0126) and locate the `Info.plist` file, which in located in the  `./Payload/MASTestApp.app/Info.plist`.
2. Run the `run.sh` script to parse the Info.plist and find all occurrences of permission related keys (those ending in `UsageDescription`).

{{ run.sh }}

### Observation

The output lists all the permission found in the `Info.plist` file. This provides a clear and immediate overview of the app's intended capabilities and access to user data.

{{ output.txt }}

### Evaluation

The test fails because the app's `Info.plist` declares an excessive number of permissions (e.g., Location, Contacts, Camera, HealthKit) that are not justified by the app's core functionality.