---
platform: android
title: References to File APIs for Writing Data Unencrypted to the App Sandbox
id: MASTG-TEST-0x01
type: [static]
weakness: MASWE-0006
best-practices: [MASTG-BEST-0x01]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0x01, MASTG-KNOW-0041]
---

## Overview

This test uses static analysis to look for uses of Java File APIs (see @MASTG-KNOW-0x01) that write data to the app's internal storage (see @MASTG-KNOW-0041). These include [`Context.openFileOutput`](https://developer.android.com/reference/android/content/Context#openFileOutput(java.lang.String,%20int)), [`FileOutputStream`](https://developer.android.com/reference/java/io/FileOutputStream), and [`FileWriter`](https://developer.android.com/reference/java/io/FileWriter).

Static analysis is great for identifying all code locations where the app is writing data to internal storage. However, it does not reveal the actual data being written at runtime. To confirm that sensitive data is written unencrypted, combine this test with the dynamic counterpart @MASTG-TEST-0x02.

## Steps

1. Reverse engineer the app (@MASTG-TECH-0017).
2. Run a static analysis (@MASTG-TECH-0014) tool on the reverse engineered app targeting calls to File APIs that write data to internal storage.

## Observation

The output should contain a list of locations in the code where the app uses File APIs that may write data to the app's internal storage.

## Evaluation

The test case fails if the app uses File APIs to write data to internal storage and you can confirm (by reviewing the relevant code) that:

- sensitive data is being written; **and**
- the data is not encrypted before being written (e.g., no `Cipher` encryption calls precede the write).
