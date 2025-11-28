---
platform: android
title: Runtime Verification of Sensitive Data Stored Unencrypted in Android Room DB
id: MASTG-TEST-0309
type: [dynamic]
weakness: MASWE-0006
best-practices: [MASTG-BEST-0025]
profiles: [L1, L2]
status: new
---

## Overview

This test checks at runtime whether sensitive data — tokens, secrets, or PII — is stored in Room databases without encryption. The goal is to ensure that sensitive information is not persisted in plaintext within the app's private storage.

## Steps

1. Install and run the app on a rooted or emulated device (@MASTG-TECH-0005).

2. Trigger app functionality that processes or stores sensitive data.

3. Access the app's private storage and locate Room database files:
   - `/data/data/<package_name>/databases/<database_name>`
   - `/data/data/<package_name>/databases/<database_name>-wal`
   - `/data/data/<package_name>/databases/<database_name>-shm`
   (@MASTG-TECH-0008)

4. Extract the database files to the host machine using @MASTG-TECH-0003.

5. Inspect database contents using a SQLite client or dynamic analysis tool (@MASTG-TECH-0015) to confirm whether sensitive data is stored in plaintext.

## Observation

- Which Room database files exist on the device
- Whether sensitive data (tokens, secrets, PII) is stored in plaintext

## Evaluation

The test fails if sensitive data in Room database files can be read in plaintext and no encryption mechanism (e.g., SQLCipher) is applied.
