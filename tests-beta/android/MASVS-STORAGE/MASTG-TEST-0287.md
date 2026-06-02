---
title: References to Sensitive Data Stored Unencrypted via the SharedPreferences API to the App Sandbox
platform: android
id: MASTG-TEST-0287
type: [static, code]
weakness: MASWE-0006
best-practices: [MASTG-BEST-0x31]
profiles: [L1, L2]
prerequisites:
- identify-sensitive-data
knowledge: [MASTG-KNOW-0036]
---

## Overview

When apps use the `SharedPreferences` API to store sensitive data without encryption, the data is written to plain-text XML files in the app's sandbox at `/data/data/<package-name>/shared_prefs/`. While `MODE_PRIVATE` restricts file access to the app itself, it doesn't protect the data from being read by attackers who gain access to the device's file system (for example, through device compromise, backup extraction, or physical access to rooted/unlocked devices).

This test uses runtime instrumentation to detect when the app writes data via `SharedPreferences` and determines whether sensitive data is being stored unencrypted.

## Steps

1. Install the app on a device (@MASTG-TECH-0005).
2. Make sure you have @MASTG-TOOL-0145 installed on your machine and the Frida server running on the device.
3. Execute a method trace (@MASTG-TECH-0033) targeting the `SharedPreferences.Editor` methods that write data:
    - `SharedPreferences.Editor.putString`
    - `SharedPreferences.Editor.putStringSet`
4. Include tracing of cryptographic APIs to help determine if values are encrypted:
    - `javax.crypto.Cipher.*`
    - `java.security.KeyStore.*`
    - `javax.crypto.KeyGenerator.*`
    - `android.util.Base64.*`
5. Exercise the app, navigating through various workflows and inputting data that may be considered sensitive.

## Observation

The output should contain a list of all calls to `SharedPreferences` write methods, including the keys, values, and stack traces showing where in the app's code these calls originate. The trace should also include any related cryptographic operations that may indicate encryption is being used.

## Evaluation

The test case fails if sensitive data is written to `SharedPreferences` without being encrypted first.

To evaluate the output:

1. **High-level trace inspection**: Review the sequence of calls to identify if `SharedPreferences.Editor.putString` or `putStringSet` calls are preceded by `Cipher` operations. Values written without prior encryption are likely stored in cleartext.

2. **Pattern matching**: Use a secrets detection tool (for example, @MASTG-TOOL-0144) to scan the output for known secret patterns such as API keys, tokens, passwords, or private keys.

3. **Manual verification**: Use the stack traces to navigate to the relevant code locations in the reversed app (@MASTG-TECH-0117) and trace back the source of the values being written to confirm whether they contain sensitive data and whether encryption is applied.

4. **File system inspection**: Retrieve the `SharedPreferences` XML files from the app's sandbox using @MASTG-TOOL-0004 and inspect their contents to verify whether sensitive data is stored in cleartext.
