---
platform: android
title: Uses of Xposed/LSPosed Detection Techniques
id: MASTG-DEMO-0x4A
code: [kotlin]
test: MASTG-TEST-0x49
tools: [MASTG-TOOL-0110]
kind: pass
---

## Sample

The snippet below shows sample code that performs four Xposed/LSPosed/Frida detection techniques used by Android apps as anti-instrumentation checks (see @MASTG-KNOW-0030 for more information about Detection of Reverse Engineering Tools). The checks combine a `PackageManager` lookup for known framework Manager package ids, a reflection-based tripwire on guaranteed-native methods, a `/proc/self/maps` scan for foreign DEX/APK mappings injected into the process, and a stack-trace / thread probe that surfaces framework frames and native worker threads.

- **Check 1 — Classic Manager-package lookup.** Targets a curated list of known LSPosed / Xposed / EdXposed Manager package ids declared in `<queries>` in the manifest, and calls `PackageManager.getPackageInfo(pkg, 0)` for each. Catches lazy installs of the Manager.  

- **Check 2 — Classic method-descriptor verification.** Resolves a set of guaranteed-native methods via reflection (`System.currentTimeMillis` / `nanoTime`, `Object.notify` / `notifyAll`, `Thread.currentThread`) and checks that `Modifier.NATIVE` is still set on each. LSPlant clears `kAccNative` whenever it hooks a native method, and Frida's Java bridge does the same — so a missing bit on any of these methods is a strong tripwire. Only fires when the attacker actually targets one of the audited methods.

- **Check 3 — Foreign DEX/APK in `/proc/self/maps`.** Reads the app's own memory map and flags any `/data/app/<pkg>/base.apk` path that does not belong to the app itself. LSPosed `mmap`s a module's `base.apk` into every hooked process. Permission-free.

- **Check 4 — Stack-trace and thread inspection.** Forces three exceptions through calls an attacker is likely to hook — `PackageManager.getPackageInfo`, `Runtime.exec`, and `File.exists` — and scans each `Throwable.stackTrace` for class names belonging to known instrumentation frameworks. Also walks `Thread.getAllStackTraces()` and `/proc/self/task/<tid>/comm` to catch native worker threads (Frida's `gum-js-loop`, etc.) that have no Java `Thread` representation.

{{ MastgTest.kt # MastgTest_reversed.java # AndroidManifest.xml }}

## Steps

Run the static analysis rule against the decompiled code.

{{ ../../../../rules/mastg-android-xposed-detection.yaml }}

{{ run.sh }}

## Observation

The output shows the semgrep rule matching all four detection routines in the reversed Java.

{{ output.txt }}

## Evaluation

The test passes because the app statically implements four independent Xposed/LSPosed/Frida detection mechanisms — semgrep flags:

- `PackageManager.getPackageInfo` against the known Manager package ids (`de.robv.android.xposed.installer`, `org.lsposed.manager`, `org.meowcat.edxposed.manager`, `io.va.exposed`, `com.solohsu.android.edxp.manager`).
- The `Modifier.isNative` tripwire on the audited guaranteed-native methods.
- The `/proc/self/maps` reader paired with `/data/app/` inspection.
- The stack-trace / `/proc/self/task` probe via `Throwable.getStackTrace` / `Thread.getAllStackTraces`.

!!! note

   This is a "kind: pass" demo for static presence only: the sample contains the detection mechanisms expected by the test. The dynamic counterpart @MASTG-DEMO-0x4B covers a Frida-based runtime bypass that defeats all four of these checks.
