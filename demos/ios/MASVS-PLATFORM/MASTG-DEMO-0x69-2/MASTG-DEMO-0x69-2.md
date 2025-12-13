---
platform: ios
title: Declaring Dangerous Permissions in entitlements.plist
code: [xml, swift]
id: MASTG-DEMO-0x69-1
test: MASTG-TEST-0069
---

### Sample

The code snippet below shows sample code that accesses protected resources requiring entitlements. The app's `entitlements.plist` file declares multiple permissions that the app uses to request special access.

{{ MastgTest.swift }}


### Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Run `run.sh` (@MASTG-TECH-0111) to extract the entitlements.

{{ run.sh }}

### Observation

The output reveals the entitlements declared in the app's entitlements.plist file.

{{ output.txt }}

### Evaluation

The test fails because the app declares multiple entitlements that may be excessive for its core functionality.