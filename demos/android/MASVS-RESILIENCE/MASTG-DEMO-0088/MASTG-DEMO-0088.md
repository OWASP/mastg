---
platform: android
title: App Terminating on Frida Hook Detection Before Cipher.doFinal Execution
id: MASTG-DEMO-0088
code: [kotlin]
test: MASTG-TEST-03te
kind: pass
---

## Sample

This sample encrypts and decrypts a sensitive API key using AES/GCM via the Android KeyStore. Unlike the unprotected variant in @MASTG-DEMO-0087, this version includes a runtime hook detection mechanism that reads `/proc/self/maps` to check for the presence of Frida-related libraries (e.g., `frida-agent`, `frida-gadget`). If detected, the app terminates the process immediately via `Process.killProcess()` before any cryptographic operations are performed.

{{ MastgTest.kt }}

## Steps

1. Install the app on a device (@MASTG-TECH-0005)
2. Use @MASTG-TECH-0033 to target `Cipher.dofinal()` API
3. Run `run.sh` to spawn the app with Frida
4. Click the **Start** button
5. Observe that the app terminates before the hooks can capture any data

{{ script.js # run.sh }}

## Observation

The output contains no instances of `Cipher` method calls found at runtime. The terminal output from the `run.sh` command is `Process terminated`.

{{ output.txt }}

## Evaluation

The test passes because the hooking attempt fails due to the app's defensive response. The app detects the Frida agent by scanning `/proc/self/maps` for entries containing "frida" or "gadget" and terminates the process via `Process.killProcess()`. The process terminates before `Cipher.doFinal()` hooks execute, so no sensitive data is extracted.

!!! note
    Even if the test case passes, it might still be possible to bypass the app's defensive response. For example, an attacker could hook the `detectHooking()` method itself or lower level APIs such as the file reading APIs to hide Frida from the process memory map. @MASTG-KNOW-0030 and @MASTG-KNOW-0032 describe such challenges.
