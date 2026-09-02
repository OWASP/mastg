---
platform: android
title: Runtime Use of File APIs for Writing Data Unencrypted to the App Sandbox
id: MASTG-TEST-0x02
type: [dynamic]
weakness: MASWE-0006
best-practices: [MASTG-BEST-0x01]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0x01, MASTG-KNOW-0041]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0x01.

It uses runtime method hooking to identify whether sensitive data is written unencrypted to the app's internal storage by monitoring Java File API calls (see @MASTG-KNOW-0x01) such as `Context.openFileOutput`, `FileOutputStream.write`, and `FileWriter.write`. Correlating these calls with any Cipher or KeyStore API calls lets you determine whether the data is encrypted before being written.

## Steps

1. Install the app on a device (@MASTG-TECH-0005).
2. Make sure you have @MASTG-TOOL-0145 installed on your machine and the frida-server running on the device.
3. Run the @MASTG-TOOL-0145 hook configuration targeting File APIs and related cryptographic APIs.
4. Exercise app features that could handle sensitive data (authentication flows, session establishment, offline caching, profile editing, or token refresh logic).
5. Stop the script.

## Observation

The output should contain a list of calls to File APIs that write data to the app sandbox. A backtrace is also provided to help identify the corresponding locations in the code.

## Evaluation

The test case fails if you can find sensitive data written to the app sandbox without prior encryption via File APIs.

Determining whether the data is encrypted or not may require careful analysis. Correlate the `FileOutputStream.write` or `openFileOutput` calls with any `Cipher`, `KeyStore`, or `KeyGenerator` calls to determine whether encryption was applied before writing.
