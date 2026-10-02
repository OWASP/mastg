---
platform: android
title: Behavioral Detection of Hook Detection Mechanisms
id: MASTG-DEMO-0x03
code: [kotlin]
test: MASTG-TEST-0x04
kind: pass
---

## Sample

This demo compares the behavior of the sample app from @MASTG-DEMO-0107 on a device without hooking applied to the application against a device with hooking applied at application startup.

Hooking is applied to the `open` function of the `libc` loaded in the application.

In this demo, the app responds to hook detection by terminating the process immediately via `Process.killProcess()` before the cryptographic operations over the example data (which is considered sensitive) are performed.

{{ ../MASTG-DEMO-0107/MastgTest.kt }}

## Steps

1. Use @MASTG-TECH-0005 to install the app on a clean, unrooted device.
2. Open the app.
3. Tap **Start** and observe the displayed results.
4. Use @MASTG-TECH-0005 to install the same app on a device rooted with @MASTG-TOOL-0021.
5. Make sure you have @MASTG-TOOL-0031 installed on your machine and the frida-server running on the rooted device.
6. Run `run.sh` to spawn the app with @MASTG-TOOL-0031 and hook the `open` function of the `libc` loaded in the application at startup.
7. Tap **Start** and compare its response with the results without hooking applied.

{{ script.js # run.sh }}

## Observation

The app behaves differently on the two devices. It performs the cryptographic operations (encryption/decryption of sensitive data) in the clean device, but does not perform it in the device with that has hooking applied to the `open` function in the `libc` loaded in the application.

### Device without hooking applied

When **Start** is tapped, cryptographic operations are performed over the sensitive data. The application prints the encrypted and decrypted values:

{{ output-clean.txt }}

### Rooted device with hooking applied

When **Start** is tapped, the app terminates its process before performing any cryptographic operations.

{{ output.txt }}

## Evaluation

The test passes because the app performs cryptographic operations in a device without hooking applied to the application and terminates before performing them when hooking is applied to the `open` function in the `libc`  loaded in the application.
