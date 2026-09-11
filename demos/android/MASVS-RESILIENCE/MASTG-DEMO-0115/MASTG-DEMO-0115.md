---
platform: android
title: JDWP Debugging Checks in App Code
id: MASTG-DEMO-0115
code: [kotlin]
test: MASTG-TEST-0352
kind: pass
---

## Sample

This sample detects an attached JDWP debugger at runtime by calling `Debug.isDebuggerConnected()`.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run @MASTG-TOOL-0110 with the following rule:

{{ ../../../../rules/mastg-android-debugger-checks.yml }}

{{ run.sh }}

## Observation

The output lists code locations where runtime debugger checks are performed.

{{ output.txt }}

## Evaluation

The test passes because the app uses `Debug.isDebuggerConnected()` API to detect an attached JDWP debugger at line 23.
