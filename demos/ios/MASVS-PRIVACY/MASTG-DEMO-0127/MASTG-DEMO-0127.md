---
platform: ios
title: Unjustified Capability Exposure due to Excessive Entitlements
id: MASTG-DEMO-0127
code: [swift, xml]
test: MASTG-TEST-0363
---

## Sample

This sample uses the same app as @MASTG-DEMO-0126. The app binary is signed with the `com.apple.developer.healthkit` entitlement, which allows the app to request user authorization for HealthKit access. This dummy app does not need the information provided by such entitlement for its functionality. Indeed, the Swift code does not import HealthKit, instantiate `HKHealthStore`, or request access to HealthKit data types.

This runtime demo traces representative HealthKit APIs associated with the `com.apple.developer.healthkit` entitlement while exercising the app and verifies if the related APIs are called.

{{ ../MASTG-DEMO-0126/MastgTest.swift # ../MASTG-DEMO-0126/Info.plist # ../MASTG-DEMO-0126/MASTestApp.entitlements }}

## Steps

1. Use @MASTG-TOOL-0129 with its `-OC` option to extract the entitlements from the signed app bundle and save the output as `entitlements_reversed.plist`.
2. Install the app on a device (@MASTG-TECH-0056).
3. Make sure you have @MASTG-TOOL-0039 installed on your machine and the frida-server running on the device.
4. Run `run_frida.sh` to spawn the app with Frida.
5. Tap the **Start** button to exercise the sample flow.
6. Stop the script by pressing `Ctrl+C`.

{{ run.sh # script.js # run_frida.sh }}

## Observation

`entitlements_reversed.plist` shows the entitlements embedded in the app:

{{ entitlements_reversed.plist }}

The Frida script output shows the HealthKit runtime hooks or class lookup result captured while exercising the app:

{{ output_frida.txt }}

## Evaluation

The test case fails because the app is signed with the `com.apple.developer.healthkit` entitlement, but the exercised runtime flow does not show any HealthKit API use such as `HKHealthStore`. The sample app does not present any health, fitness, or wellness feature that would justify enabling HealthKit.
