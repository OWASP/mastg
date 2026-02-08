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

- **`java.io.File.<init>` calls for `su` path checks:**
    - From `MastgTest.checkForSuBinary()` at line 56.
    - The app checks for 10 `su` binary locations such as `/system/app/Superuser.apk`, `/sbin/su`, `/system/bin/su`, `/system/xbin/su`, etc.
    - Additional constructor calls from libraries like `androidx.profileinstaller.ProfileInstaller` may appear in the trace but are not related to root detection.

- **`PackageManager.getPackageInfo` calls for root packages:**
    - From `MastgTest.checkForRootPackages` at line 94.
    - The app checks for 12 root management packages including `com.topjohnwu.magisk`, `eu.chainfire.supersu`, etc. In the captured output, the relevant `getPackageInfo` calls throw `NameNotFoundException` as expected since none of these packages are installed.
    - Additional `getPackageInfo` calls from system or library components may appear in the trace but are unrelated to root detection.

- **`Runtime.exec` calls for system property checks:**
    - From `MastgTest.getSystemProperty` at line 155, called by `checkForDangerousProps` at line 139.
    - The app executes `getprop ro.debuggable` and `getprop ro.secure` commands to read system properties.
