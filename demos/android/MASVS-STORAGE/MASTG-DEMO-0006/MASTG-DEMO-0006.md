---
platform: android
title: Tracing Common Logging APIs Looking for Authenticators
id: MASTG-DEMO-0006
code: [kotlin]
test: MASTG-TEST-0203
---

## Sample

The snippet contains many calls to logging APIs which are used to print out authenticators such as API keys.

{{ MastgTest.kt }}

## Steps

Execute `frida-trace` against the sample app, tracing logging classes and methods.

{{ run.sh }}

## Observation

`frida-trace` has identified several instances where log output has been printed.

{{ output.txt }}

As a reference, this is the corresponding logcat output obtained from Android Studio.

{{ logcat_output.txt }}

## Evaluation

Review each of the reported instances looking for known authenticators (e.g. API keys).

Note: You could refine the test to input a known authenticators and then search for it in the logs.
