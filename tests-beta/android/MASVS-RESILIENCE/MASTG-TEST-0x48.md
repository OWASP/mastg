---
platform: android
title: Runtime Use of Frida Detection Techniques
id: MASTG-TEST-0x48
type: [dynamic, hooks]
weakness: MASWE-0098
best-practices: [MASTG-BEST-0x48]
profiles: [R]
knowledge: [MASTG-KNOW-0030]
---

## Overview

The test verifies whether the app's Frida detection logic can be trivially neutralized by hooking the Java or system APIs it relies on. If the app implements only standard local-environment signature checks — such as connecting to the default `frida-server` TCP port (`127.0.0.1:27042`), walking `/proc/self/task/<tid>/comm` for Frida worker thread names like `gum-js-loop`, `gmain`, or `pool-frida`, and scanning `/proc/self/maps` for artifacts like `frida-agent.so` or `libfrida`, an attacker with full control over the host device can spoof clean responses from each underlying API and disable the protection at runtime. This can lead to instrumentation going undetected, allowing the attacker to inspect or modify sensitive runtime behavior despite the apparent presence of an anti-Frida defense.

## Steps

1. Install the app on a device with `frida-server` running.
2. Use @MASTG-TOOL-0001 to attach to the app and test the function that perform Frida detection.
3. Run @MASTG-TECH-0033 to attempt to hook the APIs used by each detection routine to return clean values.

## Observation

The output should contain a list of detection routines that the app executed, the APIs they queried, and the value those APIs returned with a Frida script attached.

## Evaluation

The test case fails if the app continues to operate normally after each detection routine receives spoofed clean values from the hooked APIs, indicating that the Frida detection logic can be neutralized at the API level.
