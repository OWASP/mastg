---
platform: android
title: Static Analysis for Unencrypted Sensitive Data in SQLite
id: MASTG-TEST-0304
type: [static]
weakness: MASWE-0006
best-practices: [MASTG-BEST-0025]
profiles: [L2]
status: new
---

## Overview

This test verifies whether the app's code uses SQLite APIs to store sensitive data — such as tokens, credentials, or PII — without encryption. By default, SQLite databases created using `SQLiteOpenHelper` or `context.openOrCreateDatabase` are not encrypted.

## Steps

1. Obtain the application package (APK) using @MASTG-TECH-0003.

2. Use static analysis (@MASTG-TECH-0014) to review code for calls to SQLite APIs, including:
   - `SQLiteOpenHelper`
   - `context.openOrCreateDatabase`
   - `SQLiteDatabase#insert`, `query`, `execSQL`, etc.

3. Check database creation and insertion logic to determine if:
   - the database is created using default SQLite,
   - no encryption mechanism (SQLCipher or equivalent) is applied,
   - sensitive fields (tokens, secrets, PII) are inserted in plaintext.

## Observation

- Identify SQLite databases created in the code.
- Determine whether sensitive data is inserted without encryption.

## Evaluation

The test fails if the app references SQLite APIs and stores sensitive data without any encryption.
