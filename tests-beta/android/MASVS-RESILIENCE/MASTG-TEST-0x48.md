---
platform: android
title: Testing Runtime Use of Frida
id: MASTG-TEST-0x48
type: [dynamic]
weakness: MASWE-0098
best-practices: [MASTG-BEST-0x48]
profiles: [R]
knowledge: [MASTG-KNOW-0030]
---

## Overview

If an application relies exclusively on standard, local environment signatures to detect instrumentation frameworks, an attacker with full control over the host device can easily bypass these checks. 

Commonly implemented detection patterns—such as attempting connections to the default Frida-server TCP port (`127.0.0.1:27042`), searching `/proc/<pid>/cmdline` for strings like `frida-server` or `frida-helper`, and scanning `/proc/self/maps` for artifacts like `frida-agent.so`, `libfrida`, or `gum-js-loop`—provide insufficient resilience. Because these checks reside entirely within the user space controlled by the analyst, they represent superficial barriers. This test verifies whether the application's runtime protection can be trivially neutralized by hooking the underlying Java or system APIs to spoof expected clean-device responses.

## Steps

1. Install the app on a device with `frida-server` running.
2. Use @MASTG-TOOL-0001 to attach to the app and test the function that perform Frida detection.
3. Run @MASTG-TECH-0033 to attempt to hook the APIs used by each detection routine to return clean values.

## Observation

The output should contain a list of detection routines that the app executed, the APIs they queried, and the value those APIs returned with a Frida script attached.

## Evaluation

The test case fails if the app fails to detect Frida when `frida-server` is attached to the process.
