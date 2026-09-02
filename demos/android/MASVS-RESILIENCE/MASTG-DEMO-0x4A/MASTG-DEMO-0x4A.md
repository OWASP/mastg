---
platform: android
title: Static Detection of Xposed/LSPosed Hooks using semgrep
id: MASTG-DEMO-0x4A
code: [kotlin]
test: MASTG-TEST-0x49
tools: [MASTG-TOOL-0110]
kind: pass
---

## Sample

The snippet below shows sample code that performs two Xposed/LSPosed detection techniques used by Android apps as anti-instrumentation checks. The checks combine a `/proc/self/maps` scan for foreign DEX/APK mappings injected into the process and a stack-trace probe that surfaces framework frames left by hooked methods and parked framework workers.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the sample code.

{{ ../../../../rules/mastg-android-xposed-detection.yml }}

{{ run.sh }}

## Observation

The output contains the locations of all Xposed/LSPosed detection checks in the code.

{{ output.txt }}

## Evaluation

The test case passes because the app statically implements two independent Xposed/LSPosed detection mechanisms:

- Line 114 opens `/proc/self/maps` to scan for foreign DEX/APK mappings injected into the process address space.
- Line 153 declares the framework needle string literals (`de.robv.android.xposed`, `org.lsposed.lspd`, `lsphooker_`, `lsplant`, `edxposed`, `re.frida`) used to inspect stack traces via `Throwable.getStackTrace` and `Thread.getAllStackTraces` for Xposed/LSPosed bridge frames.
