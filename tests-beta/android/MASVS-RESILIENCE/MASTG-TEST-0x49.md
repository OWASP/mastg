---
platform: android
title: Runtime Use of Xposed/LSPosed Detection Techniques
id: MASTG-TEST-0x49
apis: [PackageManager, Method, BufferedReader, Throwable, Thread, File]
type: [dynamic, hooks]
weakness: MASWE-0098
best-practices: [MASTG-BEST-0x49]
profiles: [R]
knowledge: [MASTG-KNOW-0030]
---

## Overview

This test verifies whether the app detects Xposed/LSPosed at runtime using common local-environment signatures, such as a `/proc/self/maps` scan for foreign APK/DEX mappings injected into the process (an LSPosed module's `base.apk`) and a stack-trace probe that forces exceptions through likely-hooked methods (`PackageManager.getPackageInfo`, `Runtime.exec`, `File.exists`) and scans the captured `Throwable.stackTrace` plus every live thread's `Thread.getAllStackTraces` for framework class names (`de.robv.android.xposed.*`, `org.lsposed.lspd.*`, `LSPHooker_`). Because these checks rely entirely on user-space APIs controlled by the attacker, they can be silently disabled by hooking the underlying Java calls to return the values expected on a clean device, leaving the framework and its modules undetected.

## Steps

1. Use @MASTG-TECH-0005 to install the app on a rooted device with LSPosed active and at least one module scoped to the app.
2. Use @MASTG-TECH-0043 to hook the relevant API calls.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

 The output should contain a list of detection routines that the app executed, the APIs they queried, and the value those APIs returned both without and with a Frida script attached.

## Evaluation

The test case fails if the app continues to operate normally after each detection routine receives spoofed clean values from the hooked APIs, indicating that the Xposed/LSPosed detection logic can be neutralized at the API level.
