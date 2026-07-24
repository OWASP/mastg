---
platform: android
title: Runtime Verification of Sensitive Data Stored Unencrypted in DataStore
id: MASTG-TEST-0308
type: [dynamic, filesysttem]
weakness: MASWE-0006
best-practices: [MASTG-BEST-0025]
profiles: [L2]
status: new
---

## Overview

This test checks at runtime whether sensitive data — tokens, passwords, or PII — is stored in DataStore files in plaintext. The goal is to verify that sensitive information is not left unencrypted in the app's sandbox.

## Steps

1. Install and run the app on a rooted or emulated device (@MASTG-TECH-0005).

2. Trigger app functionality that processes or stores sensitive data.

3. Access the app's private storage (typically `/data/data/<package_name>/datastore/`) to locate DataStore files:
   - `.preferences_pb` (Preferences DataStore)
   - `.proto` (Proto DataStore)
   (@MASTG-TECH-0008)

4. Extract the DataStore files to the host machine using @MASTG-TECH-0003.

5. Inspect file contents using dynamic analysis techniques (@MASTG-TECH-0015) to determine whether sensitive data is stored in plaintext.
   _Note: Proto DataStore files require a Proto decoder for inspection._

## Observation

- Which DataStore files exist on the device.
- Whether sensitive data (tokens, secrets, PII) is present in plaintext or easily reversible format.

## Evaluation

The test fails if sensitive data is stored in DataStore files without encryption and can be read in plaintext through static or dynamic analysis.
