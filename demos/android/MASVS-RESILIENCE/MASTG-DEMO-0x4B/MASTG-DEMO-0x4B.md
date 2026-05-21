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

This sample uses the same code as @MASTG-DEMO-0x4A, which performs four Xposed/LSPosed/Frida detection techniques: a `PackageManager.getPackageInfo()` lookup for known framework Manager package ids, a reflection-based `Modifier.isNative()` tripwire on guaranteed-native methods, a `/proc/self/maps` scan for foreign DEX/APK mappings injected into the process, and a stack-trace / `/proc/self/task` probe that surfaces framework frames and native worker threads. This demo defeats all four with a Frida script that intercepts the Java APIs each detection routine relies on: `ApplicationPackageManager.getPackageInfo`, `Method.getModifiers`, `BufferedReader.readLine` (over `/proc/self/maps`), `Throwable.getStackTrace` / `Thread.getStackTrace` / `Thread.getAllStackTraces`, and `File.listFiles` (over `/proc/self/task`).

See @MASTG-KNOW-0030 and @MASTG-KNOW-0032 for more context on bypassing runtime detection mechanisms.

!!! note
    This is a series of correlated tests.

    - @MASTG-DEMO-0x4A is a successful test (successful defense) that defends against Xposed/LSPosed/Frida detection mechanisms.
    - This test is a failed test (failed defence/successful attack) against the defenses of @MASTG-DEMO-0x4A by using a runtime API-hooking bypass.

{{ ../MASTG-DEMO-0x4A/MastgTest.kt # script.js }}

## Steps

1. Install the app from @MASTG-DEMO-0x4A on a device where the Xposed/LSPosed framework is active and at least one module is scoped to `org.owasp.mastestapp` (@MASTG-TECH-0005).
2. Spawn the app with the Frida bypass attached:

{{ run.sh }}

3. Tap **Start** in the app and compare the result with @MASTG-DEMO-0x4A on the same device.

## Observation

The Frida console shows every hook firing while the on-screen result reports **PASS** for all four checks — and, critically, the liability prompt that @MASTG-DEMO-0x4A raises on detection never appears.

{{ output.txt }}

## Evaluation

The test fails because every detection routine has been neutralized at runtime:

- **Manager-package lookup.** A `getPackageInfo` hook throws `NameNotFoundException` for every known Xposed/LSPosed Manager package id, so `checkKnownXposedManagerPackages()` returns an empty list.
- **Method-modifier tripwire.** A `Method.getModifiers` hook re-ORs `Modifier.NATIVE` on every audited method whose bit has been cleared, so `checkHookedMethodSignatures()` reports the methods as untouched.
- **`/proc/self/maps` parsing.** A recursion-safe `BufferedReader.readLine` filter drops every `/proc/self/maps` line containing an Xposed-related package id before it reaches the app, so `checkForeignDexesInMaps()` returns empty.
- **Stack-trace and thread probe.** A `Throwable.getStackTrace` hook strips framework frames from every captured stack, and a `File.listFiles` hook hides instrumentation tids from the `/proc/self/task` walk before the app reads their `comm` files. Both halves of the check come back clean.
