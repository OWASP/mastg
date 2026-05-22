---
platform: android
title: Bypassing Frida Detection via API Hooking
id: MASTG-DEMO-0x49
code: [kotlin]
test: MASTG-TEST-0x48
tools: [MASTG-TOOL-0031]
kind: fail
---

## Sample

This demo defeats three Frida detection checks with a Frida script that hooks the Java APIs each routine depends on (`Socket.connect`, `File.listFiles`, `BufferedReader.readLine`) and reconfigures `frida-server` on different port.

{{ ../MASTG-DEMO-0x48/MastgTest.kt # script.js }}

## Steps

1. Install the app, then push and rename `frida-server` on the device and start it on a non-default port.
2. Spawn the app with the Frida bypass attached.

{{ run.sh }}

## Observation

The Frida console shows every hook firing while the app reports **PASS** for all three Frida-detection checks.

{{ output.txt }}

## Evaluation

The test case fails because every detection routine has been bypassed at runtime:

- The `Socket.connect` hook raises `ConnectException` for `127.0.0.1:27042`, and `frida-server` was moved to a non-default port.
- The `File.listFiles` hook filters `/proc` to drop Frida-named pids, and the `frida-server` binary was renamed to `notfrida`.
- The `BufferedReader.readLine` hook drops `/proc/self/maps` lines mentioning Frida artifacts (`frida-agent`, `libfrida`, `frida-gadget`, `gum-js-loop`, `linjector`).
