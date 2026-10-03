---
platform: android
title: Behavioral Detection of Frida Detection Mechanisms
id: MASTG-DEMO-0x03
code: [kotlin, cpp]
test: MASTG-TEST-0x04
kind: pass
---

## Sample

This sample extends @MASTG-DEMO-0107 by also adding security logic to the native layer, as well as additional checks against Frida and hooking in general. The sample encrypts and decrypts a sensitive API key using AES/GCM via the Android KeyStore in both Java and Native layers. 

Native code uses the platform `Cipher` through JNI. Before each sensitive native operation, an inlined check looks for the Frida trampoline on ARM64 or x86_64. A second check verifies bionic's code integrity by calculating a quick FNV-1a checksum of its `.text` section and comparing it against its file contents in disk. The already existing check from @MASTG-DEMO-0107 scans `/proc/self/maps` for indicators of frida injection. 

See @MASTG-KNOW-0030 and @MASTG-KNOW-0032 for more context on bypassing runtime detection mechanisms.

!!! note
    This is a series of correlated tests.

    - @MASTG-DEMO-0106 is a failed test (failed defence/successful attack) against a data exfiltration attack.
    - @MASTG-DEMO-0107 is a successful test (successful defense/failed attack) against the attack of @MASTG-DEMO-0106.
    - @MASTG-DEMO-0108 is a failed test (failed defence/successful attack) against the defenses of @MASTG-DEMO-0107 by using a more "complex" attack.
    - This is a successful test (successful defense/failed attack) against the attack of @MASTG-DEMO-0108.

{{ MastgTest.kt # native-key.cpp # CMakeLists.txt }}

## Steps

1. Install the app on a clean, unrooted device using @MASTG-TECH-0005.
2. Install the app on a rooted device using @MASTG-TECH-0005 and ensure @MASTG-TOOL-0x02 is available.
3. In the rooted device, run `run.sh` to spawn the app with Frida attached and tracing calls to `open()`.
4. Tap **Start** in both devices and compare the app's response between the two.
5. Stop Frida by pressing `Ctrl+C`.

{{ run.sh }}

## Observation

### Clean Device

No frida indicators were found in the device.

{{ output-clean.txt }}

### Instrumented Device

The output does not contain injection-related detections inside `/proc/self/maps` due to the use of a stealthier Frida build (@MASTG-TOOL-0x02) that patches common sources of detection, such as the inspected `frida` and `gadget` indicators inside the demo code. 

Nevertheless, more advanced detections such as the integrity checks over the `libc` library were triggered, causing the demo to abort sensitive operations in native code.

{{ output-instrumented.txt # frida-output-instrumented.txt }}

## Evaluation

The test passes because the multi-layered detection approach implemented in the demo successfully detected the attack. Although `/proc/self/maps` based detections were not sufficient due to the use of @MASTG-TOOL-0x02, later checks in native code successfully detected Frida due to its manipulation over Bionic, Android's libc.

!!! note "Frida Instrumentation Internals"
    Frida automatically hooks different bionic functions on spawn or attach as part of its setup process, causing any checks over bionic's code integrity to fail just by having Frida inside an application. As a result, the integrity checks present in this demo would also fail without needing to hook `open()`. These are Frida internals that may change in the future, reducing the tool's detection surface and thus, the test intentionally hooks a common symbol such as `open()` to create a controlled detection point.
