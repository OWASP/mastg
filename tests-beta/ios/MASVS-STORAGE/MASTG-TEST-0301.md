---
platform: ios
title: Runtime Use of APIs for Storing Unencrypted Data in Private Storage
id: MASTG-TEST-0301
type: [dynamic, hooks]
profiles: [L2]
weakness: MASWE-0006
best-practices: [MASTG-BEST-0024]
knowledge: [MASTG-KNOW-0091, MASTG-KNOW-0057, MASTG-KNOW-0108]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0300 and is designed to be used together with @MASTG-TEST-0302.

It uses runtime method hooking to identify if sensitive data is written unencrypted to private storage or directly in the Keychain by monitoring file and Keychain APIs.

Note that some of the target APIs route I/O through system daemons or otherwise avoid direct `open` and `write` syscalls, so you'll have to hook the relevant Objective C or Swift APIs rather than tracing syscalls only.

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0095 to hook the relevant APIs.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain:

- A list of calls to the relevant Keychain APIs
- A list of calls to the relevant File System APIs

## Evaluation

The test case fails if the sensitive data is not encrypted before being written to private storage or the Keychain API isn't used to store the sensitive data.

Determining whether data is encrypted when written to private storage may be challenging. However, by monitoring the APIs used for writing data and analyzing the data written, you can infer whether encryption is being applied based on the methods and libraries used. You'll have to correlate the data written to private storage with the APIs used to write it, as identified through runtime method hooking. You'll also have to correlate the File System APIs with the Keychain APIs to verify that they are used together to store sensitive data securely. Sensitive data can be stored securely in the Keychain or be encrypted using a key from the Keychain before being written to private storage.
