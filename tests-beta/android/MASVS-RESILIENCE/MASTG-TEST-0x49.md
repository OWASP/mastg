---
platform: android
title: Runtime Use of Xposed/LSPosed Detection Mechanisms
id: MASTG-TEST-0x49
apis: [PackageManager, Method, BufferedReader, Throwable, Thread, File]
type: [dynamic]
weakness: MASWE-0098
best-practices: [MASTG-BEST-0x49]
profiles: [R]
knowledge: [MASTG-KNOW-0030]
---

## Overview

Well-known Xposed/LSPosed detection patterns a `PackageManager.getPackageInfo` lookup for known Manager package ids, a reflection-based `Modifier.isNative` tripwire on guaranteed-native methods, a `/proc/self/maps` scan for foreign DEX/APK mappings, and a stack-trace / `/proc/self/task` probe for framework frames and native worker threads provide little resilience against an attacker that controls the device. Each of these signals can be neutralized with a few lines of Frida JavaScript that hooks the underlying Java APIs and returns the values the detection logic expects on a clean device.

This test checks whether an app's Xposed/LSPosed detection routines actually resist a runtime API-hooking bypass.

## Steps

1. Install the app on a rooted device with LSPosed active and at least one module scoped to the app.
2. Use @MASTG-TOOL-0001 to attach to the app and test the function that perform Xposed/LSPosed detection.
3. Run @MASTG-TECH-0033 to attempt to hook the APIs used by each detection routine to return clean values.

## Observation

The output should contain a list of detection routines that the app executed, the APIs they queried, and the value those APIs returned both with the a frida script attached.

## Evaluation

The test case fails if the app fails to detect Xposed/LSPosed when at least one LSPosed module is loaded into the process.
