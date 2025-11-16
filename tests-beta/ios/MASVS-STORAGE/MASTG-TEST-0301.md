---
platform: ios
title: Runtime Use of APIs for Storing Unencrypted Data in Private Storage
id: MASTG-TEST-0301
type: [dynamic]
profiles: [L2]
weakness: MASWE-0006
best-practices: [MASTG-BEST-0024]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0x52-1 and is designed to be used together with @MASTG-TEST-0x52-3.

It uses runtime method hooking to identify if sensitive data is written unencrypted to private storage (@MASTG-KNOW-0108) or directly in the Keychain by monitoring file (@MASTG-KNOW-0091) and Keychain (@MASTG-KNOW-0057) APIs.

Note that some of the target APIs route I/O through system daemons or otherwise avoid direct `open` and `write` syscalls, so you'll have to hook the relevant Objective C or Swift APIs rather than tracing syscalls only.

## Steps

1. Use runtime method hooking (see @MASTG-TECH-0095) and look for uses of file system APIs that create or write files.
2. Use runtime method hooking (see @MASTG-TECH-0095) and look for uses of Keychain APIs.
3. Exercise app features that could handle sensitive data (authentication flows, session establishment, offline caching, profile viewing/editing, cryptographic operations, secure messaging, payment, or token refresh logic).

## Observation

The output should contain:

- A list of calls to the relevant Keychain APIs
- A list of calls to the relevant File System APIs

## Evaluation

The test case fails if the sensitive data is not encrypted before being written to private storage or the Keychain API isn't used to store the sensitive data.

Determining whether data is encrypted when written to private storage may be challenging. However, by monitoring the APIs used for writing data and analyzing the data written, you can infer whether encryption is being applied based on the methods and libraries used. You'll have to correlate the data written to private storage with the APIs used to write it, as identified through runtime method hooking. You'll also have to correlate the File System APIs with the Keychain APIs to verify that they are used together to store sensitive data securely. Sensitive data can be stored securely in the Keychain or be encrypted using a key from the Keychain before being written to private storage.
