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

This test verifies whether app detect well-known Frida detection patterns — connecting to the default `frida-server` TCP port (`127.0.0.1:27042`), grepping `/proc/<pid>/cmdline` for `frida-server`/`frida-helper`, or scanning `/proc/self/maps` for `frida-agent.so`/`libfrida`/`gum-js-loop` — provide little resilience against an attacker that controls the device. Each of these signals can be neutralized with a few lines of Frida JavaScript or by reconfiguring the `frida-server` deployment (renaming the binary, binding a non-default port). If these mechanisms are the only line of defense, an attacker can trivially run instrumentation against the app and the protection is illusory.

This test checks whether an app's Frida detection routines actually resist an in-process Frida bypass that hooks the underlying `Socket`/`File`/`FileReader` APIs and returns the values the detection logic expects when no instrumentation is active.

## Steps

1. Install the app on a device with `frida-server` running.
2. Use @MASTG-TOOL-0001 to attach to the app and exercise the function(s) that perform Frida detection.
3. Use @MASTG-TECH-0033 to attempt to hook the APIs used by each detection routine (`java.net.Socket.connect`, `java.io.File.listFiles`, `java.io.BufferedReader.readLine`) to return clean values.
4. Re-run the app's detection function and capture the result.

## Observation

The output should contain a list of detection routines that the app executed, the APIs they queried, and the value those APIs returned both with and without the bypass script attached.

## Evaluation

The test case fails if the app fails to detect Frida when app normally under `frida-server` is running and a generic API-hooking bypass is attached.
