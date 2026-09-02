---
platform: android
title: Bypassing Xposed/LSPosed Detection via API Hooking
id: MASTG-DEMO-0x4B
code: [kotlin]
test: MASTG-TEST-0x49
tools: [MASTG-TOOL-0031]
kind: fail
---

## Sample

This demo defeats both Xposed/LSPosed detection checks from @MASTG-DEMO-0x4A with a Frida script that hooks the Java APIs each routine depends on (`BufferedReader.readLine`, `Throwable.getStackTrace`).

!!! note
    This is a series of correlated tests.
    - @MASTG-DEMO-0x4A is a successful test (successful defense/failed attack) against an Xposed/LSPosed instrumentation attack.
    - This test is a failed test (failed defense/successful attack) against the defenses of @MASTG-DEMO-0x4A by using a more "complex" attack.

{{ ../MASTG-DEMO-0x4A/MastgTest.kt }}

## Steps

1. Install the app on a device (@MASTG-TECH-0005) where the Xposed/LSPosed framework is active and at least one module is scoped to `org.owasp.mastestapp`.
2. Make sure you have @MASTG-TOOL-0031 installed on your machine and the frida-server running on the device.
3. Run `run.sh` to spawn the app with the bypass script.
4. Click the **Start** button.
5. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI.

{{ script.js # run.sh }}

## Observation

The output contains the trace lines emitted by each hook as it intercepts a detection probe, while the app reports **PASS** for detection checks.

{{ output.txt }}

## Evaluation

The test case fails because every detection routine has been bypassed at runtime:

- The `BufferedReader.readLine` hook drops `/proc/self/maps` lines whose mapped path contains an Xposed-related package id, so the foreign-DEX scan returns empty.
- The `Throwable.getStackTrace` hook strips frames whose class name matches a framework needle (`de.robv.android.xposed`, `org.lsposed.lspd`, `lsphooker_`, …), so the stack-trace probe finds no Xposed/LSPosed frames.
