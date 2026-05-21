---
platform: android
title: Runtime Use of Xposed/LSPosed Detection Mechanisms
id: MASTG-TEST-0x49
apis: [PackageManager, Method, BufferedReader, Throwable, Thread, File]
type: [dynamic]
weakness: MASWE-0098
best-practices: [MASTG-BEST-0x49]
profiles: [R]
knowledge: [MASTG-KNOW-0030]
---

## Overview

Android apps that try to detect the Xposed/LSPosed framework with classic Java-only techniques fall into two camps:

- **Easily-bypassed-and-often-silent** techniques mirrored from the MASTG-TEST-0048 v1 guidance — a targeted `PackageManager.getPackageInfo` lookup for known Manager package ids, and reflection-based verification that `Modifier.NATIVE` is still set on a curated list of guaranteed-native methods. These remain useful as low-cost tripwires for lazy attacker setups but are silent against any modern LSPosed install with "Hide LSPosed Manager" enabled and against module deployments that don't touch the methods the app audits.
- **Stronger** techniques that actually fire against modern LSPosed 1.9+ on Android 12+ — scanning `/proc/self/maps` for foreign DEX/APK mappings that LSPosed introduces into every hooked process, and a stack-trace probe through likely-hooked methods (plus `/proc/self/task/<tid>/comm` enumeration to catch native Frida workers).

This test verifies whether an app's Xposed/LSPosed detection actually resists a modern LSPosed deployment plus a generic Frida-based bypass that hooks the underlying Java APIs.

The four detection techniques exercised by the linked demo are:

- **Manager-package lookup** via `PackageManager.getPackageInfo` against a fixed list declared in `<queries>` (`de.robv.android.xposed.installer`, `org.lsposed.manager`, `org.meowcat.edxposed.manager`, `io.va.exposed`, `com.solohsu.android.edxp.manager`).
- **Method-descriptor verification** via `java.lang.reflect.Method.getModifiers()` for a small set of guaranteed-native methods (`System.currentTimeMillis` / `nanoTime`, `Object.notify` / `notifyAll`, `Thread.currentThread`). If `Modifier.NATIVE` is cleared on any one of them, LSPlant or Frida has hooked the method.
- **Foreign DEX/APK in `/proc/self/maps`** — flag any `/data/app/<pkg>/base.apk` mapping whose package id is not the app itself. LSPosed `mmap`s a module's APK into every hooked process.
- **Stack-trace inspection + native-thread enumeration** — force exceptions through methods an attacker is likely to hook (`PackageManager.getPackageInfo`, `Runtime.exec`, `File.exists`) and scan the resulting `Throwable.stackTrace` for framework class names (`de.robv.android.xposed.*`, `org.lsposed.lspd.*`, `LSPHooker_<id>`, `re.frida.*`, …). Also walks `Thread.getAllStackTraces()` and `/proc/self/task/<tid>/comm` to catch native instrumentation threads (`gum-js-loop`, `gmain`, `pool-frida`).

When any of these detections fires, the app MUST alert the user and require liability acceptance before continuing (see the dialog pattern in the linked demos).

## Steps

1. Install the app on a rooted device with Magisk + Zygisk + LSPosed 1.9+ active and at least one LSPosed module scoped to the app.
2. Run @MASTG-TOOL-0001 to attach to the app and apply a generic Java-side bypass that hooks `ApplicationPackageManager.getPackageInfo` (to throw `NameNotFoundException` for Manager package ids), `Method.getModifiers` (to re-OR `Modifier.NATIVE` on the audited methods), `BufferedReader.readLine` (to scrub `/proc/self/maps` of foreign DEX/APK paths), `Throwable.getStackTrace` / `Thread.getStackTrace` / `Thread.getAllStackTraces` (to strip framework frames), and `File.listFiles` against `/proc/self/task` (to hide instrumentation tids).
3. Trigger the app's Xposed detection function and capture the result.

## Observation

The output should contain a list of detection routines, the APIs they queried (`PackageManager.getPackageInfo`, `Method.modifiers`, `/proc/self/maps`, `Throwable.stackTrace`, `Thread.getAllStackTraces`, `/proc/self/task/<tid>/comm`), and the value those APIs returned with and without the bypass attached. It should also indicate whether the app raised the liability-acceptance dialog when the checks fired.

## Evaluation

The test case fails if **any** of the following hold:

- The app reports "no Xposed detected" while at least one LSPosed module is loaded into the process and a generic Java-side bypass is attached, or
- A check fires but the app does not raise an alert/liability-acceptance dialog to the user before proceeding.

Note that this Java-side test only covers the API-hooking bypass category. Bypass via app-code patching and via kernel-level syscall interception are out of scope and must be addressed by complementary controls (DEX/native integrity hashing, signing-certificate pinning, and server-side attestation such as the Play Integrity API).
