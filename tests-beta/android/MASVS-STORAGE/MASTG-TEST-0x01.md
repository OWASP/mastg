---
platform: android
title: Runtime Storage of Unencrypted Data in Room Databases
id: MASTG-TEST-0x01
type: [dynamic, filesystem]
weakness: MASWE-0006
best-practices: [MASTG-BEST-0x25]
profiles: [L2]
status: new
---

## Overview

This test complements @MASTG-TEST-0x02. It checks at runtime whether sensitive data (like tokens, secrets, or PII) is stored in Room databases without encryption.

If the app stores sensitive data in the Room databases, such as tokens, credentials, or PII, without integrating an encryption layer like SQLCipher (@MASTG-KNOW-0038), anyone who gains access to the app's sandbox (via physical access or backup extraction) can extract the plaintext data.

The goal is to ensure that sensitive information is not persisted in plaintext in the Room databases within the app's private storage.

## Steps

1. Exercise all the functionalities of the app that process or store sensitive data.

2. Access the app's private storage (@MASTG-TECH-0008) and locate Room database files:
   - `/data/data/<package_name>/databases/<database_name>`
   - `/data/data/<package_name>/databases/<database_name>-wal`
   - `/data/data/<package_name>/databases/<database_name>-shm`

3. Extract the database files of the app to the host machine (@MASTG-TECH-0002).

4. Inspect database contents using a SQLite client or dynamic analysis tool to confirm whether sensitive data is stored in plaintext.

## Observation
The output should contain:
- The location of the Room database files inside the application's private storage.
- Occurrences inside the Room database file where the sensitive data (tokens, secrets, PII) is stored in plaintext.

## Evaluation

The test fails if sensitive data in Room database files can be read in plaintext and no encryption mechanism (e.g., SQLCipher) is applied.
