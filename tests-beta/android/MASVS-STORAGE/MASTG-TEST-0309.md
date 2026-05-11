---
platform: android
title: Runtime Verification of Sensitive Data Stored Unencrypted in Android Room DB
id: MASTG-TEST-0309
type: [dynamic, filesystem]
weakness: MASWE-0006
best-practices: [MASTG-BEST-0025]
profiles: [L2]
status: new
---

## Overview

This test checks at runtime whether sensitive data — tokens, secrets, or PII — is stored in Room databases without encryption. The goal is to ensure that sensitive information is not persisted in plaintext within the app's private storage.

## Steps


1. Exercise all the functionalities of the app that process or store sensitive data.

2. Access the app's private storage (@MASTG-TECH-0008) and locate Room database files:
   - `/data/data/<package_name>/databases/<database_name>`
   - `/data/data/<package_name>/databases/<database_name>-wal`
   - `/data/data/<package_name>/databases/<database_name>-shm`

3. Extract the database files of the app to the host machine (@MASTG-TECH-0002).

4. Inspect database contents using a SQLite client or dynamic analysis tool (@MASTG-TECH-0015) to confirm whether sensitive data is stored in plaintext.

## Observation

- The location of the Room database files inside the application's private storage.
- Occurrences inside the Room database file where the sensitive data (tokens, secrets, PII) is stored in plaintext.

## Evaluation

The test fails if sensitive data in Room database files can be read in plaintext and no encryption mechanism (e.g., SQLCipher) is applied.
