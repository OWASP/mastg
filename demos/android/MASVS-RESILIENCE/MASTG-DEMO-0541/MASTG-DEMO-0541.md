---
platform: android
title: Runtime Detection of Root Detection Mechanisms
id: MASTG-DEMO-0541
code: [kotlin]
test: MASTG-TEST-0502
tools: [MASTG-TOOL-0145]
---

## Sample

This demo shows how to detect root detection mechanisms at runtime using Frooky. The sample app from @MASTG-DEMO-0540 implements multiple root detection checks.

{{ ../MASTG-DEMO-0540/MastgTest.kt }}

## Steps

1. Ensure the target app is installed on the device and frida-server is running.
2. Run @MASTG-TOOL-0145 with the hooks configuration to monitor root detection methods.

{{ run.sh }}

The hooks configuration monitors common root detection methods:

{{ hooks.json }}

## Observation

The output shows all root detection method invocations captured during app execution.

{{ output.json }}

## Evaluation

The test passes because the output confirms the app implements root detection checks that were monitored at runtime:

- **20 File constructor calls** (from `MastgTest.checkForSuBinary()` at line 68): By hooking the File constructor instead of `exists()`, we can see the actual paths being checked. The app checks for su binaries at 10 unique locations: `/system/app/Superuser.apk`, `/sbin/su`, `/system/bin/su`, `/system/xbin/su`, etc. Each path check is visible twice because the test function was invoked twice.

- **24 PackageManager.getPackageInfo() calls** (from `MastgTest.checkForRootPackages()` at line 96): The app checks for 12 root management packages including `com.topjohnwu.magisk`, `eu.chainfire.supersu`, etc. All throw `NameNotFoundException` as expected since none are installed. Each package is checked twice due to two test invocations.

- **4 Runtime.exec() calls** (from `MastgTest.getSystemProperty()` at line 133, called by `checkForDangerousProps()` at line 123): The app executes `getprop ro.debuggable` and `getprop ro.secure` commands to read system properties that may indicate root access. Each property is checked twice.

The output contains 50 total hook events: 48 from the root detection test (20 File + 24 PackageManager + 4 Runtime.exec) and 2 from system components. The stack traces confirm all root detection checks originate from `mastgTest()` being invoked twice, showing comprehensive root detection monitoring with minimal noise.
