---
title: Missing Validation of Data Returned from Implicit Intents
platform: android
id: MASTG-TEST-0375
type: [dynamic, hooks, manual]
weakness: MASWE-0083
best-practices: [MASTG-BEST-0056, MASTG-BEST-0057]
knowledge: [MASTG-KNOW-0025, MASTG-KNOW-0138]
profiles: [L1, L2]
---

## Overview

Applications that use implicit intents to request data (such as files) from other applications must properly validate and sanitize the data received in the `onActivityResult` callback. When an implicit intent is used (whether it is a standard action like `GET_CONTENT` or a custom action), any app on the device can potentially respond. A malicious responder can return unexpected URIs (like `file://` instead of `content://`) or malicious metadata (like filenames containing path-traversal strings `../`).

If the receiving app trusts this data without validation, it can lead to severe vulnerabilities such as arbitrary file read or arbitrary code execution (see @MASTG-KNOW-0138).

This test focuses on the broader issue of improper verification of data returned by third-party components.

## Steps

### Static Analysis

1. Use @MASTG-TECH-0014 to scan the application's source code or decompiled codebase for instances where data is received via an intent response (e.g., in `onActivityResult`).
2. Analyze the flow of the returned data (URI or metadata) to identify where it is used in sensitive operations, such as file I/O or dynamic code loading.
3. Verify if the application implements robust sanitization and validation on the data. For example:
    - Checking that a URI uses the expected `content://` scheme and not a local `file://` scheme.
    - Validating that a filename does not contain path-traversal sequences like `../`.
    - Ensuring the final resolved file path is within the intended directory.

### Dynamic Analysis

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to hook file system constructors (e.g., `java.io.File`, `java.io.FileOutputStream`).
3. Trigger the implicit intent and provide a malicious response from a controlled attacker app.
4. Observe the parameters passed to the file system hooks.
5. Verify if the instrumentation detects path traversal or attempts to write to sensitive internal directories.

## Observation

The output should contain the file system operations triggered when the malicious intent response is processed, including the full paths passed to file constructors and output streams.

## Evaluation

The test case fails if data from an intent response is used in sensitive operations without proper sanitization, or if providing a malicious intent response results in unauthorized file access or path-traversal behavior.
