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

The snippet below shows sample code that performs two Xposed/LSPosed detection techniques used by Android apps as anti-instrumentation checks. The checks combine a `/proc/self/maps` scan for foreign DEX/APK mappings injected into the process and a stack-trace probe that surfaces framework frames left by hooked methods and parked framework workers.

{{ MastgTest.kt # MastgTest_reversed.java # AndroidManifest.xml }}

## Steps

Let's run our semgrep rule against the sample code.

{{ ../../../../rules/mastg-android-xposed-detection.yaml }}

{{ run.sh }}

## Observation

The rule has identified some instances in the code file where detection checks are placed.

{{ output.txt }}

## Evaluation

TThe test case passes because the app statically implements two independent Xposed/LSPosed detection mechanisms:

- Line 114 opens `/proc/self/maps` to scan for foreign DEX/APK mappings injected into the process address space.
- Line 153 declares the framework needle string literals (`de.robv.android.xposed`, `org.lsposed.lspd`, `lsphooker_`, `lsplant`, `edxposed`, `re.frida`) used to inspect stack traces via `Throwable.getStackTrace` and `Thread.getAllStackTraces` for Xposed/LSPosed bridge frames.
