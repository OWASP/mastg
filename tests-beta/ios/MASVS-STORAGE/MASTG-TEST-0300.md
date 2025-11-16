---
platform: ios
title: References to APIs for Storing Unencrypted Data in Private Storage
id: MASTG-TEST-0300
type: [static]
profiles: [L2]
best-practices: [MASTG-BEST-0024]
weakness: MASWE-0006
---

## Overview

This test checks whether the app writes unencrypted sensitive data to private storage (@MASTG-KNOW-0108). It focuses on:

- APIs that persist data in the app sandbox directories (@MASTG-KNOW-0091), including Foundation `FileManager` methods, low-level POSIX and BSD file I/O calls and high-level APIs such as `UserDefaults`, Core Data and SQLite wrappers.
- Keychain APIs (@MASTG-KNOW-0057) used to:
    - store sensitive data directly within the Keychain
    - manage keys from the Keychain (that could be used to encrypt data before writing to private storage).

## Steps

1. Run a static analysis tool such as @MASTG-TOOL-0073 and look for uses of file system APIs that create or write files.
2. Run a static analysis tool such as @MASTG-TOOL-0073 and look for uses of Keychain APIs.

## Observation

The output should contain:

- A list of locations where the app writes or may write data to private storage.
- A list of locations where the app uses Keychain APIs, including access control and accessibility attributes.

## Evaluation

The test case fails if the sensitive data is not encrypted before being written to private storage or the Keychain API isn't used to store the sensitive data.
