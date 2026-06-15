---
title: Runtime Storage of Unencrypted Data via the SharedPreferences API
platform: android
id: MASTG-TEST-0287
type: [dynamic, hooks, manual]
weakness: MASWE-0006
best-practices: [MASTG-BEST-0050]
profiles: [L1, L2]
prerequisites:
- identify-sensitive-data
knowledge: [MASTG-KNOW-0036]
---

## Overview

In Android, applications can use the [`SharedPreferences`](https://developer.android.com/reference/android/content/SharedPreferences) API to store sensitive data without encryption, typically under the app's private data directory, such as `/data/user/0/<package-name>/shared_prefs/` or `/data/data/<package-name>/shared_prefs/`.

While `MODE_PRIVATE` restricts file access to the app itself, it doesn't protect the data from being read by attackers who gain access to the device's file system (for example, through device compromise, backup extraction, or physical access to rooted/unlocked devices).

This test uses runtime instrumentation to detect when the app writes data via `SharedPreferences` and determines whether sensitive data is being stored unencrypted.

Relevant API calls regarding `SharedPreferences` include `SharedPreferences.Editor.putString(...)` and `putStringSet(...)`, which write string values to the XML files in the app's sandbox. There's also `put*` methods for other data types, but strings are the most common way to store sensitive data such as API keys, tokens, passwords, or private keys.

For encryption, relevant API calls include `javax.crypto.Cipher`, `java.security.KeyStore`, or `javax.crypto.KeyGenerator`.

For more information about the `SharedPreferences` API, refer to @MASTG-KNOW-0036.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to hook the relevant API calls.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.
4. Use @MASTG-TECH-0008 to retrieve the app's `SharedPreferences` XML files.

## Observation

The output should contain a list of all calls to `SharedPreferences` write methods, including the keys, values, and stack traces showing where in the app's code these calls originate. The trace should also include related cryptographic operations that may indicate encryption is being used.

The output should also contain the contents of all `SharedPreferences` XML files.

## Evaluation

The test case fails if sensitive data is written to `SharedPreferences` without being encrypted first.

**Further Validation Required:**

1. **High-level trace inspection**: Review the sequence of calls from the hook output to identify if `SharedPreferences.Editor.putString` or `putStringSet` calls are preceded by `Cipher` operations. Values written without prior encryption are likely stored in cleartext.
2. **Pattern matching**: Use a secrets detection tool (for example, @MASTG-TOOL-0144) to scan the output for known secret patterns such as API keys, tokens, passwords, or private keys.
3. **Manual verification**: Use the stack traces from the hook output to navigate to the relevant code locations in the reversed app (@MASTG-TECH-0023) and trace back the source of the values being written to confirm whether they contain sensitive data and whether encryption is applied.
