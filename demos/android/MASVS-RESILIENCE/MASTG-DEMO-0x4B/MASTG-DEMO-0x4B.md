---
platform: android
title: Bypass of Xposed/LSPosed Detection via PackageManager, Method-Modifier, Maps and Stack-Trace Hooks
id: MASTG-DEMO-0x4B
code: [kotlin]
test: MASTG-TEST-0x49
tools: [MASTG-TOOL-0031]
kind: fail
---

## Sample

This demo defeats four Xposed/LSPosed detection checks with a Frida script that hooks the Java APIs each routine depends on (`getPackageInfo`, `Method.getModifiers`, `BufferedReader.readLine`, `Throwable.getStackTrace`, `File.listFiles`).

{{ ../MASTG-DEMO-0x4A/MastgTest.kt # script.js }}

## Steps

1. Install the app on a device where the Xposed/LSPosed framework is active and at least one module is scoped to `org.owasp.mastestapp`.
2. Spawn the app with the Frida bypass attached.

{{ run.sh }}

## Observation

The Frida console shows every hook firing while the app reports **PASS** for all four detection checks.

{{ output.txt }}

## Evaluation

The test case fails because every detection routine has been bypassed at runtime:

- The `getPackageInfo` hook throws `NameNotFoundException` for every known Xposed/LSPosed Manager id.
- The `Method.getModifiers` hook re-sets `Modifier.NATIVE` on audited methods.
- The `BufferedReader.readLine` hook drops lines mentioning Xposed-related package ids.
- The `Throwable.getStackTrace` hook strips framework frames, and the `File.listFiles` hook hides instrumentation tids from `/proc/self/task`.
