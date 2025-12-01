---
platform: android
title: Runtime Verification of Sensitive Data Stored Unencrypted in SQLite
id: MASTG-TEST-0307
type: [dynamic]
weakness: MASWE-0006
best-practices: [MASTG-BEST-0025]
profiles: [L2]
status: new
---

## Overview

This test checks at runtime whether sensitive data — tokens, credentials, or PII — is stored in SQLite databases in plaintext. The goal is to verify that data stored in the app's private storage is not left unencrypted.

## Steps

1. Install and run the app on a rooted or emulated device (@MASTG-TECH-0005).

2. Trigger app functionality that processes or stores sensitive data.

3. Access the app's private storage to locate SQLite database files (e.g., `.db`, `.sqlite`, `.sqlite3`) (@MASTG-TECH-0008).

4. Extract the database files to the host machine using @MASTG-TECH-0003.

5. Inspect the database content using dynamic analysis techniques (@MASTG-TECH-0015) to determine if sensitive data is stored in plaintext.

## Observation

- Which SQLite databases exist on the device.
- Whether sensitive data (tokens, secrets, PII) is present in plaintext.

## Evaluation

The test fails if sensitive data is stored in SQLite without encryption and can be read in plaintext through static or dynamic analysis.
