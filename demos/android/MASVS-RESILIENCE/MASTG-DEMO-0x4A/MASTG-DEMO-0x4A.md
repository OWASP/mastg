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

The snippet below shows sample code that performs four Xposed/LSPosed/Frida detection techniques used by Android apps as anti-instrumentation checks. The checks combine a `PackageManager` lookup for known framework Manager package ids, a reflection-based tripwire on guaranteed-native methods, a `/proc/self/maps` scan for foreign DEX/APK mappings injected into the process, and a stack-trace / thread probe that surfaces framework frames and native worker threads.

{{ MastgTest.kt # MastgTest_reversed.java # AndroidManifest.xml }}

## Steps

Let's run our semgrep rule against the sample code.

{{ ../../../../rules/mastg-android-xposed-detection.yaml }}

{{ run.sh }}

## Observation

The rule has identified some instances in the code file where detection checks are placed.

{{ output.txt }}

## Evaluation

The test case passes because the app statically implements four independent Xposed/LSPosed/Frida detection mechanisms:

- Line 143 declares the list of known Xposed/LSPosed/EdXposed Manager package ids (`de.robv.android.xposed.installer`, `org.lsposed.manager`, `org.meowcat.edxposed.manager`, `io.va.exposed`, `com.solohsu.android.edxp.manager`) consumed by a `PackageManager.getPackageInfo` presence probe.
- Lines 172–174 implement the `Modifier.isNative` tripwire: LSPlant (LSPosed's hooking engine) and Frida's Java bridge clear the `kAccNative` bit when they hook a native method, and this branch flags any audited method whose reflected modifiers no longer report native.
- Line 185 opens `/proc/self/maps` from Java to scan for foreign DEX/APK mappings (e.g. an LSPosed module APK or a Frida agent `.so`) injected into the process address space.
- Lines 232 and 261 declare the framework/needle string literals (`de.robv.android.xposed`, `org.lsposed.`, `lsphooker_`, `lsplant`, `edxposed`, `re.frida`, `gum-js`, `gmain`, `pool-frida`, `linjector`, …) used to inspect stack traces via `Throwable.getStackTrace` / `Thread.getAllStackTraces`; line 263 enumerates `/proc/self/task` to cross-check `comm` values against those native worker thread needles.
