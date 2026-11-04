---
platform: android
title: Runtime Detection of Root Detection Mechanisms
id: MASTG-DEMO-0088
code: [kotlin]
test: MASTG-TEST-0325
kind: pass
---

## Sample

This demo shows how to detect root detection mechanisms at runtime using Frooky. The sample app from @MASTG-DEMO-0087 implements multiple root detection checks, and only encrypts and decrypts a sensitive API key when the checks do not detect a rooted device.

!!! note
    The sample displays individual check results, writes them and the app's reaction to the system log, and uses descriptive method names to make the detection logic easy to follow. This detail is included for learning, not as an example of production behavior. In a production app, detection diagnostics should stay internal rather than appear in the UI or logs accessible to users or third parties. Exposing them can help an attacker understand the checks, reverse engineer the app's response, and defeat those checks.

{{ ../MASTG-DEMO-0087/MastgTest.kt }}

## Steps

1. Use @MASTG-TECH-0005 to install the app on a device rooted with @MASTG-TOOL-0021 and with a root management app installed.
2. Make sure you have @MASTG-TOOL-0145 installed on your machine and the frida-server running on the device.
3. Use @MASTG-TECH-0043 to trace root detection API calls and run `run.sh` to spawn the app with the hooks configuration.
4. Tap **Start**.
5. Stop the Frida session by pressing `Ctrl+C`.

{{ hooks.json # run.sh }}

## Observation

The output shows all root detection method invocations captured during app execution.

{{ output.json }}

## Evaluation

The test passes because the output confirms the app implements root detection checks that were monitored at runtime:

- **`java.io.File.<init>` calls for `su` path checks:**
    - From `MastgTest.kt` line 133, in `checkForSuBinary()`.
    - The app checks for 10 `su` binary locations such as `/system/app/Superuser.apk`, `/sbin/su`, `/system/bin/su`, `/system/xbin/su`, etc.
    - Additional constructor calls from libraries like `androidx.profileinstaller.ProfileInstaller` may appear in the trace but are not related to root detection.

- **`Runtime.exec` calls for `which su` command:**
    - From `MastgTest.kt` line 153, in `checkForWhichSu()`.
    - The app executes the `which su` command to detect if the `su` binary is available in the system PATH.

- **`PackageManager.getPackageInfo` calls for root packages:**
    - From `MastgTest.kt` line 205, in `checkForRootPackages()`.
    - The app checks for 12 root management packages including `com.topjohnwu.magisk`, `eu.chainfire.supersu`, etc. In the captured output, the `getPackageInfo` call for `com.topjohnwu.magisk` returns a `PackageInfo` because the root management app is installed on the device, while the remaining calls throw `NameNotFoundException`.
    - Additional `getPackageInfo` calls from system or library components may appear in the trace but are unrelated to root detection.

- **`Runtime.exec` calls for system property checks:**
    - From `MastgTest.kt` line 266, in `getSystemProperty()`, called by `checkForDangerousProps()`.
    - The app executes `getprop ro.debuggable` and `getprop ro.secure` commands to read system properties.
