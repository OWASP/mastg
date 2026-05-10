---
platform: android
title: Sensitive Data Stored Unencrypted via the File APIs to the App Sandbox
id: MASTG-TEST-0x01
type: [static, dynamic]
weakness: MASWE-0006
best-practices: [MASTG-BEST-0x01]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0041]
---

## Overview

Android apps can write data directly to files in the app's internal storage using Java File APIs such as [`Context.openFileOutput`](https://developer.android.com/reference/android/content/Context#openFileOutput(java.lang.String,%20int)), [`java.io.FileOutputStream`](https://developer.android.com/reference/java/io/FileOutputStream), and [`java.io.FileWriter`](https://developer.android.com/reference/java/io/FileWriter). While files in internal storage are protected from direct access by other apps, they can be exposed if the device is rooted, the app data is extracted via an ADB backup, or the attacker exploits another vulnerability. If the app writes sensitive data (such as credentials, tokens, or personally identifiable information) to these files without encryption, that data is stored in plaintext and can be recovered by an attacker with sufficient access to the device.

This test checks whether the app uses File APIs to store sensitive data unencrypted in the app's private sandbox.

## Steps

### Static Analysis

1. Reverse engineer the app (@MASTG-TECH-0017).
2. Run a static analysis (@MASTG-TECH-0014) tool on the reverse engineered app targeting calls to internal storage file writing APIs such as `openFileOutput`, `FileOutputStream`, and `FileWriter`.

### Dynamic Analysis

1. Install the app on a device (@MASTG-TECH-0005).
2. Make sure you have @MASTG-TOOL-0145 installed on your machine and the frida-server running on the device.
3. Run `run.sh` to spawn the app with Frooky.
4. Exercise the app, navigating through the various features while paying attention to inputs of sensitive data.
5. Stop the script by pressing `Ctrl+C`.

## Observation

### Static Analysis

The output should contain a list of locations in the code where the app uses File APIs to write data to internal storage.

### Dynamic Analysis

The output should contain a list of calls to File APIs that write data to the app sandbox. A backtrace is also provided to help identify the corresponding locations in the code.

## Evaluation

### Static Analysis

The test case fails if the app uses File APIs to write data to internal storage and you can confirm (by reviewing the relevant code) that:

- sensitive data is being written; **and**
- the data is not encrypted before being written (e.g., no Cipher calls precede the write).

### Dynamic Analysis

The test case fails if you can find sensitive data written to the app sandbox without encryption via File APIs.

Determining if a string is encrypted or not may require careful analysis. Correlate the file write calls with any Cipher, KeyStore, or KeyGenerator calls to determine if encryption was applied before writing.
