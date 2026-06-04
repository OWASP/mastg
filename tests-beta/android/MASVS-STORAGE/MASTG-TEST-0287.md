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

In Android, applications can use the [`SharedPreferences`](https://developer.android.com/reference/android/content/SharedPreferences) API to store sensitive data without encryption, typically under the app's private data directory, such as `/data/user/0/<package-name>/shared_prefs/` or `/data/data/<package-name>/shared_prefs/`.

While `MODE_PRIVATE` restricts file access to the app itself, it doesn't protect the data from being read by attackers who gain access to the device's file system (for example, through device compromise, backup extraction, or physical access to rooted/unlocked devices).

This test uses runtime instrumentation to detect when the app writes data via `SharedPreferences` and determines whether sensitive data is being stored unencrypted.

For more information about the `SharedPreferences` API, refer to @MASTG-KNOW-0036.

## Steps

1. Install the app on a device (@MASTG-TECH-0005).
2. Execute a method trace (@MASTG-TECH-0033) targeting the `SharedPreferences.Editor` methods that write data:
    - `SharedPreferences.Editor.putString`
    - `SharedPreferences.Editor.putStringSet`
3. Include tracing of cryptographic APIs to help determine if values are encrypted:
    - `javax.crypto.Cipher.*`
    - `java.security.KeyStore.*`
    - `javax.crypto.KeyGenerator.*`
    - `android.util.Base64.*`
4. Exercise all workflows of the application that handle sensitive data.
 5. Retrieve the app's `SharedPreferences` XML files from the app's private data directory, typically under `shared_prefs/` (@MASTG-TECH-0008).
 6. Correlate each `SharedPreferences.Editor` write call with preceding cryptographic operations and with the values stored in the retrieved XML files.
 
## Observation

The output should contain a list of all calls to `SharedPreferences` write methods, including the keys, values, and stack traces showing where in the app's code these calls originate. The trace should also include related cryptographic operations that may indicate encryption is being used.

The output should also contain the contents of the `SharedPreferences` XML file.

## Evaluation

The test case fails if sensitive data is written to `SharedPreferences` without being encrypted first.

A write should be considered unencrypted when a sensitive value is passed to a `SharedPreferences.Editor` write method and the same value, or a trivially reversible representation of it, appears in the retrieved XML file.

A write should not be considered a failure only because the sensitive value appears in the runtime trace. Runtime hooks can capture values before encryption, so findings from pattern matching must be correlated with the value passed to `SharedPreferences.Editor` and the value stored on disk.

If the trace is inconclusive, use the stack traces to inspect the relevant code paths in the reversed app (@MASTG-TECH-0017) and confirm whether encryption is applied before the value is written.

1. **High-level trace inspection**: Review the sequence of calls to identify if `SharedPreferences.Editor.putString` or `putStringSet` calls are preceded by `Cipher` operations. Values written without prior encryption are likely stored in cleartext.

2. **Pattern matching**: Use a secrets detection tool (for example, @MASTG-TOOL-0144) to scan the output for known secret patterns such as API keys, tokens, passwords, or private keys.

3. **Manual verification**: Use the stack traces to navigate to the relevant code locations in the reversed app (@MASTG-TECH-0117) and trace back the source of the values being written to confirm whether they contain sensitive data and whether encryption is applied.

4. **File system inspection**: Retrieve the `SharedPreferences` XML files from the app's sandbox using @MASTG-TOOL-0004 and inspect their contents to verify whether sensitive data is stored in cleartext.
